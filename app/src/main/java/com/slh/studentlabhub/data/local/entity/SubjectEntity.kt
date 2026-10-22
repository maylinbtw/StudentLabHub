package com.slh.studentlabhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val semester: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val classroomCourseId: String? = null,
)
