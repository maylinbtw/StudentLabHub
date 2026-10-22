package com.slh.studentlabhub.domain.model

enum class TaskState {
    ACTIVE,
    REDO,
    COMPLETED,
}

data class ClassroomTask(
    val id: String,
    val title: String,
    val dueAt: Long?,
    val state: TaskState,
    val gradeText: String?,
    val link: String?,
)

data class CourseTasks(
    val id: String,
    val title: String,
    val teacherName: String?,
    val link: String?,
    val tasks: List<ClassroomTask>,
)

data class UpcomingTask(
    val task: ClassroomTask,
    val courseId: String,
    val courseTitle: String,
)
