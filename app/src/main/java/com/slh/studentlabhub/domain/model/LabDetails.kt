package com.slh.studentlabhub.domain.model

data class LabDetails(
    val lab: Lab,
    val subjectTitle: String,
    val scheduleTitle: String? = null,
)