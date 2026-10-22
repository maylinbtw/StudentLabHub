package com.slh.studentlabhub.notifications

import android.content.Context
import com.slh.studentlabhub.di.AppContainer
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.ReminderTiming
import com.slh.studentlabhub.domain.model.TaskState
import com.slh.studentlabhub.ui.components.shortPrefix
import kotlinx.coroutines.flow.first

private enum class ReminderWindow(val tag: String, val millis: Long) {
    HOURS("3h", 3L * 60 * 60 * 1000),
    DAY("24h", 24L * 60 * 60 * 1000),
}

class ReminderChecker(
    private val context: Context,
    private val container: AppContainer,
) {

    suspend fun run(settings: AppSettings) {
        val store = container.reminderStore
        val now = System.currentTimeMillis()
        val reminders = container.collectReminders()
        val notified = store.notifiedKeys.toMutableSet()
        val firstRun = !store.initialized
        val canPost = ReminderNotifications.canPost(context)
        val windows = when (settings.reminderTiming) {
            ReminderTiming.DAY -> listOf(ReminderWindow.DAY)
            ReminderTiming.HOURS -> listOf(ReminderWindow.HOURS)
            ReminderTiming.BOTH -> listOf(ReminderWindow.HOURS, ReminderWindow.DAY)
        }

        reminders.forEach { reminder ->
            if (reminder.isRedo) {
                val mark = "${reminder.key}@redo"
                if (mark !in notified) {
                    val shouldPost = !firstRun && settings.redoAlerts
                    if (shouldPost && canPost) {
                        ReminderNotifications.postRedo(context, reminder)
                    }
                    if (!shouldPost || canPost) {
                        notified += mark
                    }
                }
                return@forEach
            }

            val due = reminder.dueAt ?: return@forEach
            val left = due - now
            if (left <= 0L || !settings.remindersEnabled || !canPost) return@forEach
            val reached = windows.filter { left <= it.millis }
            if (reached.isEmpty()) return@forEach
            val marks = reached.map { "${reminder.key}@${it.tag}@$due" }
            if (marks.all { it in notified }) return@forEach
            ReminderNotifications.postDeadline(context, reminder, left)
            notified += marks
        }

        val alive = reminders.map { it.key }.toSet()
        store.notifiedKeys = notified.filter { it.substringBefore('@') in alive }.toSet()
        store.initialized = true
    }
}

suspend fun AppContainer.collectReminders(): List<Reminder> {
    val classroom = classroomRepository
    val labs = labRepository
    val subjects = labs.observeSubjects().first()
    val subjectByCourse = subjects
        .filter { it.classroomCourseId != null }
        .associateBy { it.classroomCourseId }

    val classroomReminders = if (classroom.signedInEmail != null) {
        classroom.observeUpcomingTasks().first().map { upcoming ->
            Reminder(
                key = "gc_${upcoming.courseId}_${upcoming.task.id}",
                title = upcoming.task.title,
                subjectTitle = subjectByCourse[upcoming.courseId]?.title ?: upcoming.courseTitle,
                dueAt = upcoming.task.dueAt,
                isRedo = upcoming.task.state == TaskState.REDO,
            )
        }
    } else {
        emptyList()
    }

    val labReminders = labs.observePendingDeadlines().first().map { lab ->
        Reminder(
            key = "lab_${lab.labId}",
            title = "${lab.type.shortPrefix} №${lab.number} · ${lab.title}",
            subjectTitle = lab.subjectTitle,
            dueAt = lab.deadline,
            isRedo = false,
        )
    }

    return classroomReminders + labReminders
}
