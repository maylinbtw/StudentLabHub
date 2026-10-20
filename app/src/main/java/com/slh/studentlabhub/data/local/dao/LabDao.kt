package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.slh.studentlabhub.data.local.entity.LabEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabDao {
    @Insert
    suspend fun insertAll(labs: List<LabEntity>)

    @Query("SELECT * FROM labs WHERE subjectId = :subjectId ORDER BY number ASC")
    fun observeLabs(subjectId: Long): Flow<List<LabEntity>>

    @Update
    suspend fun update(lab: LabEntity)
}