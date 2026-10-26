package com.slh.studentlabhub.data.classroom

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.entity.ClassroomTaskEntity
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity
import com.slh.studentlabhub.domain.model.LabStatus
import kotlin.math.roundToInt

class ClassroomMerger(
    private val database: AppDatabase,
) {

    suspend fun merge(redoLimit: Int, archivedCourseIds: Set<String>) {
        val subjectDao = database.subjectDao()
        val labDao = database.labDao()
        val courses = database.classroomDao().getCoursesWithTasks()
        if (courses.isEmpty()) return

        database.withTransaction {
            var defaultSemester: Int? = null
            courses.forEach { courseWithTasks ->
                val course = courseWithTasks.course
                val existing = subjectDao.getByCourseId(course.id)
                if (existing == null && course.id in archivedCourseIds) return@forEach
                val subjectId = existing?.id ?: run {
                    val title = ClassroomTitles.subjectTitle(course.name)
                    val sameTitle = subjectDao.getAll().firstOrNull {
                        it.classroomCourseId == null && it.title.equals(title, ignoreCase = true)
                    }
                    if (sameTitle != null) {
                        subjectDao.setCourseLink(sameTitle.id, course.id)
                        sameTitle.id
                    } else {
                        val semester = defaultSemester ?: (subjectDao.maxSemester() ?: 1).also { defaultSemester = it }
                        subjectDao.insert(
                            SubjectEntity(
                                title = title,
                                semester = semester,
                                classroomCourseId = course.id,
                            ),
                        )
                    }
                }

                val labs = labDao.getForSubject(subjectId).toMutableList()
                courseWithTasks.tasks
                    .sortedBy { it.dueAt ?: Long.MAX_VALUE }
                    .forEach { task -> mergeTask(task, subjectId, labs, redoLimit) }
            }
        }
    }

    private suspend fun mergeTask(
        task: ClassroomTaskEntity,
        subjectId: Long,
        labs: MutableList<LabEntity>,
        redoLimit: Int,
    ) {
        val labDao = database.labDao()
        val parsed = ClassroomTitles.parseWork(task.title)
        val grade = task.gradeOutOfTen()
        val isResubmitted = task.submissionState == "TURNED_IN"
        val isRedo = redoLimit > 0 && grade != null && grade <= redoLimit && !isResubmitted
        val isSubmitted = isResubmitted || task.submissionState == "RETURNED"
        val remoteStatus = when {
            isRedo -> LabStatus.NOT_DONE
            grade != null && redoLimit > 0 && grade <= redoLimit -> LabStatus.DONE
            grade != null -> LabStatus.DEFENDED
            isSubmitted -> LabStatus.DONE
            else -> null
        }
        val score = grade?.roundToInt()?.coerceIn(0, 10)

        val linked = labs.firstOrNull { it.classroomTaskId == task.id }
            ?: labs.firstOrNull {
                it.classroomTaskId == null &&
                    parsed.number != null &&
                    it.type == parsed.type &&
                    it.number == parsed.number
            }

        if (linked == null) {
            val number = parsed.number
                ?: ((labs.filter { it.type == parsed.type }.maxOfOrNull { it.number } ?: 0) + 1)
            val entity = LabEntity(
                subjectId = subjectId,
                type = parsed.type,
                number = number,
                title = parsed.title.ifEmpty { "Задание из Classroom" },
                status = remoteStatus ?: LabStatus.NOT_DONE,
                score = score,
                deadline = task.dueAt,
                classroomTaskId = task.id,
                link = task.link,
                needsRedo = isRedo,
            )
            val id = labDao.insert(entity)
            labs += entity.copy(id = id)
            return
        }

        val status = when {
            isRedo -> LabStatus.NOT_DONE
            remoteStatus != null && remoteStatus.ordinal > linked.status.ordinal -> remoteStatus
            else -> linked.status
        }
        val updated = linked.copy(
            classroomTaskId = task.id,
            link = task.link ?: linked.link,
            deadline = task.dueAt ?: linked.deadline,
            status = status,
            score = score ?: linked.score,
            needsRedo = isRedo,
        )
        if (updated != linked) {
            labDao.update(updated)
            labs[labs.indexOf(linked)] = updated
        }
    }
}

private fun ClassroomTaskEntity.gradeOutOfTen(): Double? {
    val value = assignedGrade ?: return null
    val max = maxPoints ?: return null
    if (max <= 0.0) return null
    return value / max * 10.0
}
