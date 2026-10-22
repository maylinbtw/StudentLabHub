package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.SessionStore
import com.slh.studentlabhub.data.local.entity.ClassroomCourseEntity
import com.slh.studentlabhub.data.local.entity.ClassroomCourseWithTasks
import com.slh.studentlabhub.data.local.entity.ClassroomTaskEntity
import com.slh.studentlabhub.data.remote.ClassroomApi
import com.slh.studentlabhub.data.remote.ClassroomApiException
import com.slh.studentlabhub.data.remote.GoogleAuthManager
import com.slh.studentlabhub.data.remote.UnauthorizedException
import com.slh.studentlabhub.domain.model.ClassroomTask
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.CourseTasks
import com.slh.studentlabhub.domain.model.TaskState
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.util.Locale

class ClassroomRepositoryImpl(
    private val database: AppDatabase,
    private val api: ClassroomApi,
    private val session: SessionStore,
    private val authManager: GoogleAuthManager,
) : ClassroomRepository {

    private val dao = database.classroomDao()

    private val threshold = MutableStateFlow(session.redoThreshold)

    override val signedInEmail: String?
        get() = session.email

    override val lastSyncAt: Long?
        get() = session.lastSyncAt

    override val redoThreshold: StateFlow<Int> = threshold.asStateFlow()

    override fun setRedoThreshold(value: Int) {
        val safeValue = value.coerceIn(0, 10)
        session.redoThreshold = safeValue
        threshold.value = safeValue
    }

    override fun observeCourses(): Flow<List<CourseOverview>> =
        combine(dao.observeCoursesWithTasks(), threshold) { courses, redoLimit ->
            val now = System.currentTimeMillis()
            courses.map { it.toOverview(now, redoLimit) }
        }

    override fun observeCourseTasks(courseId: String): Flow<CourseTasks?> =
        combine(dao.observeCourseWithTasks(courseId), threshold) { course, redoLimit ->
            course?.let {
                CourseTasks(
                    id = it.course.id,
                    title = it.course.name,
                    teacherName = it.course.teacherName,
                    link = it.course.link,
                    tasks = it.tasks.map { task -> task.toDomain(redoLimit) },
                )
            }
        }

    override suspend fun completeSignIn(accessToken: String): String {
        val email = try {
            api.fetchUserEmail(accessToken)
        } catch (error: UnauthorizedException) {
            authManager.clearToken(accessToken)
            throw error
        }
        session.email = email
        return email
    }

    override suspend fun sync(accessToken: String) {
        try {
            val courses = api.fetchCourses(accessToken)
            val courseEntities = mutableListOf<ClassroomCourseEntity>()
            val taskEntities = mutableListOf<ClassroomTaskEntity>()

            courses.forEachIndexed { index, course ->
                val teachers = emptyIfForbidden { api.fetchTeacherNames(accessToken, course.id) }
                courseEntities += ClassroomCourseEntity(
                    id = course.id,
                    name = course.name,
                    section = course.section,
                    teacherName = formatTeachers(teachers),
                    link = course.link,
                    position = index,
                )

                val courseWork = emptyIfForbidden { api.fetchCourseWork(accessToken, course.id) }
                val submissions = emptyIfForbidden { api.fetchSubmissions(accessToken, course.id) }
                    .associateBy { it.courseWorkId }

                courseWork.forEach { work ->
                    val submission = submissions[work.id]
                    taskEntities += ClassroomTaskEntity(
                        id = work.id,
                        courseId = course.id,
                        title = work.title,
                        dueAt = work.dueAt,
                        maxPoints = work.maxPoints,
                        link = work.link,
                        submissionState = submission?.state,
                        assignedGrade = submission?.assignedGrade,
                        isLate = submission?.isLate ?: false,
                    )
                }
            }

            database.withTransaction {
                dao.deleteAllTasks()
                dao.deleteAllCourses()
                dao.insertCourses(courseEntities)
                dao.insertTasks(taskEntities)
            }
            session.lastSyncAt = System.currentTimeMillis()
        } catch (error: UnauthorizedException) {
            authManager.clearToken(accessToken)
            throw error
        }
    }

    override suspend fun signOut() {
        session.email?.let { email -> authManager.revokeAccess(email) }
        session.clear()
        database.withTransaction {
            dao.deleteAllTasks()
            dao.deleteAllCourses()
        }
    }

    private fun formatTeachers(names: List<String>): String? = when {
        names.isEmpty() -> null
        names.size == 1 -> names.first()
        else -> "${names.first()} +${names.size - 1}"
    }

    private suspend fun <T> emptyIfForbidden(block: suspend () -> List<T>): List<T> =
        try {
            block()
        } catch (error: ClassroomApiException) {
            if (error.code == 403 || error.code == 404) emptyList() else throw error
        }
}

private fun ClassroomTaskEntity.gradeOutOfTen(): Double? {
    val grade = assignedGrade ?: return null
    val max = maxPoints ?: return null
    if (max <= 0.0) return null
    return grade / max * 10.0
}

private fun ClassroomTaskEntity.stateFor(redoLimit: Int): TaskState {
    val grade = gradeOutOfTen()
    val isRedo = redoLimit > 0 && grade != null && grade <= redoLimit
    val isSubmitted = submissionState == "TURNED_IN" || submissionState == "RETURNED"
    return when {
        isRedo -> TaskState.REDO
        isSubmitted || grade != null -> TaskState.COMPLETED
        else -> TaskState.ACTIVE
    }
}

private fun ClassroomTaskEntity.toDomain(redoLimit: Int): ClassroomTask = ClassroomTask(
    id = id,
    title = title,
    dueAt = dueAt,
    state = stateFor(redoLimit),
    gradeText = gradeText(),
    link = link,
)

private fun ClassroomTaskEntity.gradeText(): String? {
    val grade = assignedGrade ?: return null
    val max = maxPoints
    return if (max != null && max > 0.0) {
        "${formatNumber(grade)}/${formatNumber(max)}"
    } else {
        formatNumber(grade)
    }
}

private fun formatNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else String.format(Locale.US, "%.1f", value)

private fun ClassroomCourseWithTasks.toOverview(now: Long, redoLimit: Int): CourseOverview {
    val states = tasks.associateWith { it.stateFor(redoLimit) }
    val completed = states.count { it.value == TaskState.COMPLETED }
    val redo = states.count { it.value == TaskState.REDO }
    val nearest = states
        .filter { it.value != TaskState.COMPLETED }
        .keys
        .mapNotNull { it.dueAt }
        .filter { it >= now }
        .minOrNull()
    val scores = states
        .filter { it.value != TaskState.REDO }
        .keys
        .mapNotNull { it.gradeOutOfTen() }
    return CourseOverview(
        id = course.id,
        title = course.name,
        teacherName = course.teacherName,
        tasksCount = tasks.size,
        completedCount = completed,
        redoCount = redo,
        nearestDeadline = nearest,
        averageScore = if (scores.isEmpty()) null else scores.average(),
    )
}
