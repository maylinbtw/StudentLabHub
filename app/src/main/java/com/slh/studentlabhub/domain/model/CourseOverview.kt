package com.slh.studentlabhub.domain.model

data class CourseOverview(
    val id: String,
    val title: String,
    val teacherName: String?,
    val tasksCount: Int,
    val completedCount: Int,
    val redoCount: Int,
    val nearestDeadline: Long?,
    val averageScore: Double?,
)
