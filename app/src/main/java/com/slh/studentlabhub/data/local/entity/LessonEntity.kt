package com.slh.studentlabhub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    indices = [Index("date")],
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val number: Int,
    val startMinutes: Int?,
    val endMinutes: Int?,
    val subject: String,
    val teacher: String,
    val room: String,
)
