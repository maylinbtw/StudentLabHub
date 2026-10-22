package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.LabWithSubjectTitle
import kotlinx.coroutines.flow.Flow

@Dao
interface LabDao {
    @Insert
    suspend fun insert(lab: LabEntity): Long

    @Insert
    suspend fun insertAll(labs: List<LabEntity>)

    @Query("SELECT * FROM labs WHERE subjectId = :subjectId ORDER BY type ASC, number ASC")
    fun observeLabs(subjectId: Long): Flow<List<LabEntity>>

    @Query(
        "SELECT labs.*, subjects.title AS subjectTitle FROM labs " +
            "INNER JOIN subjects ON subjects.id = labs.subjectId " +
            "WHERE labs.deadline IS NOT NULL AND labs.status = 'NOT_DONE' AND subjects.archived = 0 " +
            "ORDER BY labs.deadline ASC",
    )
    fun observePendingDeadlines(): Flow<List<LabWithSubjectTitle>>

    @Query("SELECT * FROM labs WHERE id = :id")
    suspend fun getById(id: Long): LabEntity?

    @Query("SELECT MAX(number) FROM labs WHERE subjectId = :subjectId AND type = :typeName")
    suspend fun maxNumber(subjectId: Long, typeName: String): Int?

    @Update
    suspend fun update(lab: LabEntity)

    @Query("DELETE FROM labs WHERE id = :id")
    suspend fun deleteById(id: Long)
}