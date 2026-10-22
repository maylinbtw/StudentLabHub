package com.slh.studentlabhub.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ClassroomCourseWithTasks(
    @Embedded
    val course: ClassroomCourseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "courseId",
    )
    val tasks: List<ClassroomTaskEntity>,
)