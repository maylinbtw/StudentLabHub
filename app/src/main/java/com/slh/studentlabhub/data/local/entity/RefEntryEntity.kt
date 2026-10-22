package com.slh.studentlabhub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.slh.studentlabhub.domain.model.RefTech

@Entity(
    tableName = "ref_entries",
    indices = [Index("categoryId")],
)
data class RefEntryEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val tech: RefTech,
    val title: String,
    val description: String,
    val example: String,
    val position: Int,
    val source: String? = null,
) {
    companion object {
        const val SOURCE_CUSTOM = "custom"
        const val SOURCE_TLDR = "tldr"
    }
}
