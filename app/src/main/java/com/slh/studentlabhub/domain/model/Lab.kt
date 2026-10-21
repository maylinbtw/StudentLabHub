package com.slh.studentlabhub.domain.model

data class Lab(
    val id: Long,
    val subjectId: Long,
    val number: Int,
    val title: String,
    val status: LabStatus,
    val score: Int?,
    val notes: String,
    val deadline: Long?,
)