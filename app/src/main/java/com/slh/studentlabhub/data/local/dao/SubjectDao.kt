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

    @Insert
    suspend fun insert(subject: SubjectEntity): Long

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteById(id: Long)
}