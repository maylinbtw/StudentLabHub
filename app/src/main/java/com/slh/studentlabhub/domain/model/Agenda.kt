package com.slh.studentlabhub.domain.model

data class LabDeadline(
    val labId: Long,
    val subjectId: Long,
    val subjectTitle: String,
    val type: WorkType,
    val number: Int,
    val title: String,
    val deadline: Long?,
    val needsRedo: Boolean = false,
)
