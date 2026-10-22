package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.CourseTasks
import com.slh.studentlabhub.domain.model.UpcomingTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ClassroomRepository {
    val signedInEmail: String?

    val lastSyncAt: Long?

    val redoThreshold: StateFlow<Int>

    fun setRedoThreshold(value: Int)

    fun setCourseArchived(courseId: String, archived: Boolean)

    fun setCoursesArchived(courseIds: Collection<String>, archived: Boolean)

    fun observeCourses(): Flow<List<CourseOverview>>

    fun observeCourseTasks(courseId: String): Flow<CourseTasks?>

    fun observeUpcomingTasks(): Flow<List<UpcomingTask>>

    suspend fun completeSignIn(accessToken: String): String

    suspend fun sync(accessToken: String)

    suspend fun signOut()
}
