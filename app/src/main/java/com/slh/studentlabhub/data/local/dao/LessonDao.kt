package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.slh.studentlabhub.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE date >= :fromDay ORDER BY date ASC, number ASC, id ASC")
    fun observeFrom(fromDay: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE date >= :fromDay ORDER BY date ASC, number ASC, id ASC")
    suspend fun getFrom(fromDay: Long): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE date = :day ORDER BY number ASC, id ASC")
    suspend fun getForDay(day: Long): List<LessonEntity>

    @Query("SELECT DISTINCT subject FROM lessons ORDER BY subject ASC")
    fun observeSubjectNames(): Flow<List<String>>

    @Insert
    suspend fun insertAll(lessons: List<LessonEntity>)

    @Query("DELETE FROM lessons WHERE date = :day")
    suspend fun deleteDay(day: Long)

    @Query("DELETE FROM lessons WHERE date < :beforeDay")
    suspend fun deleteBefore(beforeDay: Long)

    @Query("DELETE FROM lessons")
    suspend fun deleteAll()
}
