package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.CourseTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ClassroomRepository {
    val signedInEmail: String?

    val lastSyncAt: Long?

    val redoThreshold: StateFlow<Int>

    fun setRedoThreshold(value: Int)

    fun observeCourses(): Flow<List<CourseOverview>>

    fun observeCourseTasks(courseId: String): Flow<CourseTasks?>

    suspend fun completeSignIn(accessToken: String): String

    suspend fun sync(accessToken: String)

    suspend fun signOut()
}
