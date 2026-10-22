package com.slh.studentlabhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.slh.studentlabhub.data.local.entity.RefCategoryEntity
import com.slh.studentlabhub.data.local.entity.RefEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReferenceDao {
    @Query("SELECT * FROM ref_categories ORDER BY position ASC")
    fun observeCategories(): Flow<List<RefCategoryEntity>>

    @Query("SELECT * FROM ref_entries ORDER BY position ASC")
    fun observeEntries(): Flow<List<RefEntryEntity>>

    @Query("SELECT COUNT(*) FROM ref_entries WHERE source IS NULL")
    suspend fun countBuiltinEntries(): Int

    @Query("SELECT COUNT(*) FROM ref_entries WHERE source = 'tldr'")
    fun observeTldrCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ref_entries WHERE source = 'custom'")
    fun observeCustomCount(): Flow<Int>

    @Query("SELECT * FROM ref_categories WHERE id = :id")
    suspend fun getCategory(id: String): RefCategoryEntity?

    @Query("SELECT * FROM ref_entries WHERE id = :id")
    suspend fun getEntry(id: String): RefEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<RefCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: RefCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<RefEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: RefEntryEntity)

    @Query("DELETE FROM ref_entries WHERE id = :id")
    suspend fun deleteEntry(id: String)

    @Query("DELETE FROM ref_entries WHERE source = 'tldr'")
    suspend fun deleteTldrEntries()

    @Query("DELETE FROM ref_entries WHERE source IS NULL")
    suspend fun deleteBuiltinEntries()

    @Query("DELETE FROM ref_categories WHERE id NOT LIKE 'custom_%' AND id NOT LIKE 'tldr_%'")
    suspend fun deleteBuiltinCategories()
}
