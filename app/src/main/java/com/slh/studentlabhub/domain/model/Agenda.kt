package com.slh.studentlabhub.domain.model

data class LabDeadline(
    val labId: Long,
    val subjectId: Long,
    val subjectTitle: String,
    val type: WorkType,
    val number: Int,
    val title: String,
    val deadline: Long,
)

enum class AgendaSource {
    CLASSROOM,
    LAB,
}

data class AgendaItem(
    val key: String,
    val title: String,
    val subjectTitle: String,
    val dueAt: Long?,
    val isRedo: Boolean,
    val source: AgendaSource,
    val subjectId: Long? = null,
    val courseId: String? = null,
    val labId: Long? = null,
)
