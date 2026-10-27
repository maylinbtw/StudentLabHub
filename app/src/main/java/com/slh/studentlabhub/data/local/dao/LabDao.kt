package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.LabWithSubjectTitle
import kotlinx.coroutines.flow.Flow
import com.slh.studentlabhub.data.local.entity.LabWithSubjectRef

@Dao
interface LabDao {
    @Insert
    suspend fun insert(lab: LabEntity): Long

    @Insert
    suspend fun insertAll(labs: List<LabEntity>)

    @Query("SELECT * FROM labs")
    suspend fun getAll(): List<LabEntity>

    @Query("SELECT * FROM labs WHERE subjectId = :subjectId ORDER BY type ASC, number ASC")
    fun observeLabs(subjectId: Long): Flow<List<LabEntity>>

    @Query(
        "SELECT labs.*, subjects.title AS subjectTitle FROM labs " +
            "INNER JOIN subjects ON subjects.id = labs.subjectId " +
            "WHERE labs.status = 'NOT_DONE' AND subjects.archived = 0 " +
            "AND (labs.deadline IS NOT NULL OR labs.needsRedo = 1) " +
            "ORDER BY labs.deadline ASC",
    )
    fun observePendingDeadlines(): Flow<List<LabWithSubjectTitle>>

    @Query(
        "SELECT labs.*, subjects.title AS subjectTitle, subjects.scheduleTitle AS scheduleTitle FROM labs " +
            "INNER JOIN subjects ON subjects.id = labs.subjectId " +
            "WHERE labs.defendQueued = 1 AND labs.status != 'DEFENDED' AND subjects.archived = 0 " +
            "ORDER BY labs.type ASC, labs.number ASC",
    )
    fun observeQueuedForDefense(): Flow<List<LabWithSubjectRef>>

    @Query("UPDATE labs SET defendQueued = :queued WHERE id = :id")
    suspend fun setDefendQueued(id: Long, queued: Boolean)

    @Query("SELECT * FROM labs WHERE subjectId = :subjectId")
    suspend fun getForSubject(subjectId: Long): List<LabEntity>

    @Query("SELECT * FROM labs WHERE id = :id")
    suspend fun getById(id: Long): LabEntity?

    @Query("SELECT MAX(number) FROM labs WHERE subjectId = :subjectId AND type = :typeName")
    suspend fun maxNumber(subjectId: Long, typeName: String): Int?

    @Update
    suspend fun update(lab: LabEntity)

    @Query("DELETE FROM labs WHERE id = :id")
    suspend fun deleteById(id: Long)
}