package com.slh.studentlabhub.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SubjectWithLabs(
    @Embedded
    val subject: SubjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId",
    )
    val labs: List<LabEntity>,
)