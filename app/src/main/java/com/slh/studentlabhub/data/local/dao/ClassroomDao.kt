package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.slh.studentlabhub.data.local.entity.ClassroomCourseEntity
import com.slh.studentlabhub.data.local.entity.ClassroomCourseWithTasks
import com.slh.studentlabhub.data.local.entity.ClassroomTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassroomDao {
    @Transaction
    @Query("SELECT * FROM classroom_courses ORDER BY position ASC")
    fun observeCoursesWithTasks(): Flow<List<ClassroomCourseWithTasks>>

    @Transaction
    @Query("SELECT * FROM classroom_courses WHERE id = :courseId")
    fun observeCourseWithTasks(courseId: String): Flow<ClassroomCourseWithTasks?>

    @Transaction
    @Query("SELECT * FROM classroom_courses ORDER BY position ASC")
    suspend fun getCoursesWithTasks(): List<ClassroomCourseWithTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<ClassroomCourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<ClassroomTaskEntity>)

    @Query("DELETE FROM classroom_courses")
    suspend fun deleteAllCourses()

    @Query("DELETE FROM classroom_tasks")
    suspend fun deleteAllTasks()
}
