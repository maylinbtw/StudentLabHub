package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.slh.studentlabhub.data.local.entity.SubjectEntity
import com.slh.studentlabhub.data.local.entity.SubjectWithLabs
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Transaction
    @Query("SELECT * FROM subjects ORDER BY semester DESC, createdAt ASC")
    fun observeSubjectsWithLabs(): Flow<List<SubjectWithLabs>>

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun observeSubjectWithLabs(id: Long): Flow<SubjectWithLabs?>

    @Query("SELECT title FROM subjects WHERE id = :id")
    suspend fun getTitle(id: Long): String?

    @Insert
    suspend fun insert(subject: SubjectEntity): Long

    @Query("UPDATE subjects SET title = :title, semester = :semester WHERE id = :id")
    suspend fun update(id: Long, title: String, semester: Int)

    @Query("UPDATE subjects SET classroomCourseId = NULL WHERE classroomCourseId = :courseId")
    suspend fun clearCourseLink(courseId: String)

    @Query("UPDATE subjects SET classroomCourseId = :courseId WHERE id = :subjectId")
    suspend fun setCourseLink(subjectId: Long, courseId: String)

    @Query("SELECT MAX(semester) FROM subjects")
    suspend fun maxSemester(): Int?

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteById(id: Long)
}
