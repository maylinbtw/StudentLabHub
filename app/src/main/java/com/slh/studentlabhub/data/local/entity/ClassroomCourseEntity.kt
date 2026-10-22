package com.slh.studentlabhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classroom_courses")
data class ClassroomCourseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val section: String?,
    val teacherName: String?,
    val link: String?,
    val position: Int,
)