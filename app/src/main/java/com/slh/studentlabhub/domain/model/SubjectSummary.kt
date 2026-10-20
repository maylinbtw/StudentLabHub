package com.slh.studentlabhub.domain.model

data class SubjectSummary(
    val id: Long,
    val title: String,
    val semester: Int,
    val totalLabs: Int,
    val doneLabs: Int,
    val defendedLabs: Int,
    val averageScore: Double?,
)