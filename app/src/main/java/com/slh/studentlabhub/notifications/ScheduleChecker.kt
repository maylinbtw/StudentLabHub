package com.slh.studentlabhub.notifications

import android.content.Context
import com.slh.studentlabhub.R
import com.slh.studentlabhub.data.schedule.DefenseQueue
import com.slh.studentlabhub.di.AppContainer
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.ScheduleRefreshResult
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.components.dayLabel
import com.slh.studentlabhub.ui.components.shortList
import com.slh.studentlabhub.ui.components.timeLabel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime

class ScheduleChecker(
    private val context: Context,
    private val container: AppContainer,
) {

    suspend fun run(settings: AppSettings) {
        if (settings.scheduleGroup.isBlank()) return
        val repository = container.scheduleRepository

        val last = repository.lastSyncAt.value ?: 0L
        val result = if (System.currentTimeMillis() - last >= SYNC_INTERVAL_MS) {
            try {
                repository.refresh()
            } catch (error: CancellationException) {
                throw error
            } catch (ignored: Exception) {
                null
            }
        } else {
            null
        }

        if (!settings.scheduleAlerts || !ReminderNotifications.canPost(context)) return
        result?.let { notifyChanges(it) }
        notifyDefense()
    }

    private suspend fun notifyChanges(result: ScheduleRefreshResult) {
        val lessons = container.scheduleRepository.observeUpcomingLessons().first()
        val today = LocalDate.now()
        result.newDates.forEach { date ->
            val dayLessons = lessons.filter { it.date == date }
            val text = if (dayLessons.isEmpty()) {
                Strings.get(R.string.notif_schedule_no_lessons)
            } else {
                summary(dayLessons)
            }
            ReminderNotifications.postSchedule(
                context = context,
                key = "schedule_$date",
                title = Strings.get(R.string.notif_schedule_new, date.dayLabel(today)),
                text = text,
            )
        }
        result.changedDates.forEach { date ->
            val dayLessons = lessons.filter { it.date == date }
            ReminderNotifications.postSchedule(
                context = context,
                key = "schedule_$date",
                title = Strings.get(R.string.notif_schedule_changed, date.dayLabel(today)),
                text = if (dayLessons.isEmpty()) Strings.get(R.string.notif_schedule_no_lessons) else summary(dayLessons),
            )
        }
    }

    private suspend fun notifyDefense() {
        val now = System.currentTimeMillis()
        val time = LocalTime.now()
        val today = LocalDate.now()
        val target = when {
            time.hour >= EVENING_HOUR -> today.plusDays(1)
            time.hour in MORNING_HOURS -> today
            else -> return
        }
        val repository = container.scheduleRepository
        val lessons = repository.observeUpcomingLessons().first()
        val queued = repository.observeQueuedWorks().first()
        if (queued.isEmpty()) return
        val assigned = DefenseQueue.assign(lessons, queued, now)
        val dayLessons = lessons.filter { it.date == target && assigned[it.id].orEmpty().isNotEmpty() }
        if (dayLessons.isEmpty()) return

        val store = container.reminderStore
        val key = "defense@$target@${if (target == today) "am" else "pm"}"
        val sent = store.scheduleKeys
        if (key in sent) return

        val text = dayLessons.joinToString("\n") { lesson ->
            Strings.get(
                R.string.notif_defense_line,
                lesson.number,
                lesson.subject,
                assigned[lesson.id].orEmpty().shortList(),
            )
        }
        ReminderNotifications.postSchedule(
            context = context,
            key = "defense_$target",
            title = Strings.get(R.string.notif_defense_title, target.dayLabel(today))
                .replaceFirstChar { it.uppercase() },
            text = text,
        )
        store.scheduleKeys = (sent + key)
            .filter { runCatching { LocalDate.parse(it.split('@')[1]) >= today.minusDays(3) }.getOrDefault(false) }
            .toSet()
    }

    private fun summary(lessons: List<Lesson>): String {
        val first = lessons.minByOrNull { it.number } ?: return ""
        val count = lessons.map { it.number }.distinct().size
        return Strings.get(
            R.string.notif_schedule_summary,
            Strings.plural(R.plurals.lessons_count, count, count),
            first.timeLabel().substringBefore('–'),
            lessons.joinToString(", ") { "${it.number} ${it.subject}" },
        )
    }

    private companion object {
        const val SYNC_INTERVAL_MS = 2L * 60 * 60 * 1000
        const val EVENING_HOUR = 18
        val MORNING_HOURS = 6..9
    }
}
