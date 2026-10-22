package com.slh.studentlabhub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "classroom_tasks",
    indices = [Index("courseId")],
)
data class ClassroomTaskEntity(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val title: String,
    val dueAt: Long?,
    val maxPoints: Double?,
    val link: String?,
    val submissionState: String?,
    val assignedGrade: Double?,
    val isLate: Boolean,
)