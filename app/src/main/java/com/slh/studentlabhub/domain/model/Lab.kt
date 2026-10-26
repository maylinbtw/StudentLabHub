package com.slh.studentlabhub.domain.model

data class Lab(
    val id: Long,
    val subjectId: Long,
    val type: WorkType,
    val number: Int,
    val title: String,
    val status: LabStatus,
    val score: Int?,
    val notes: String,
    val deadline: Long?,
    val link: String? = null,
    val needsRedo: Boolean = false,
    val classroomTaskId: String? = null,
) {
    val fromClassroom: Boolean
        get() = classroomTaskId != null
}