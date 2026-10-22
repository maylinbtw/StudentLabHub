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
    @Query("SELECT * FROM subjects WHERE archived = :archived ORDER BY semester DESC, createdAt ASC")
    fun observeSubjectsWithLabs(archived: Boolean): Flow<List<SubjectWithLabs>>

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun observeSubjectWithLabs(id: Long): Flow<SubjectWithLabs?>

    @Query("SELECT title FROM subjects WHERE id = :id")
    suspend fun getTitle(id: Long): String?

    @Insert
    suspend fun insert(subject: SubjectEntity): Long

    @Insert
    suspend fun insertAll(subjects: List<SubjectEntity>)

    @Query("SELECT * FROM subjects")
    suspend fun getAll(): List<SubjectEntity>

    @Query("DELETE FROM subjects")
    suspend fun deleteAll()

    @Query("UPDATE subjects SET title = :title, semester = :semester WHERE id = :id")
    suspend fun update(id: Long, title: String, semester: Int)

    @Query("UPDATE subjects SET classroomCourseId = NULL WHERE classroomCourseId = :courseId")
    suspend fun clearCourseLink(courseId: String)

    @Query("UPDATE subjects SET classroomCourseId = :courseId WHERE id = :subjectId")
    suspend fun setCourseLink(subjectId: Long, courseId: String)

    @Query("UPDATE subjects SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("UPDATE subjects SET archived = 1 WHERE semester = :semester")
    suspend fun archiveSemester(semester: Int)

    @Query("SELECT classroomCourseId FROM subjects WHERE semester = :semester AND classroomCourseId IS NOT NULL")
    suspend fun courseIdsInSemester(semester: Int): List<String>

    @Query("SELECT MAX(semester) FROM subjects")
    suspend fun maxSemester(): Int?

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteById(id: Long)
}
