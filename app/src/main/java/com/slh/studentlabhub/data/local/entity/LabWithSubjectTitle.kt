package com.slh.studentlabhub.data.local.entity

import androidx.room.Embedded

data class LabWithSubjectTitle(
    @Embedded
    val lab: LabEntity,
    val subjectTitle: String,
)
