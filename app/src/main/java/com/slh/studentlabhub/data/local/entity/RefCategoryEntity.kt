package com.slh.studentlabhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slh.studentlabhub.domain.model.RefTech

@Entity(tableName = "ref_categories")
data class RefCategoryEntity(
    @PrimaryKey
    val id: String,
    val tech: RefTech,
    val title: String,
    val position: Int,
)
