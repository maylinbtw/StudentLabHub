package com.slh.studentlabhub.domain.model

data class SubjectDetails(
    val summary: SubjectSummary,
    val labs: List<Lab>,
)