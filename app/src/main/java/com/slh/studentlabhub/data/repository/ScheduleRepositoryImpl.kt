package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.SessionStore
import com.slh.studentlabhub.data.local.entity.LessonEntity
import com.slh.studentlabhub.data.remote.ScheduleApi
import com.slh.studentlabhub.data.schedule.ParsedLesson
import com.slh.studentlabhub.data.schedule.ScheduleParser
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.model.ScheduleRefreshResult
import com.slh.studentlabhub.domain.repository.ScheduleRepository
import com.slh.studentlabhub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.LocalTime

class ScheduleRepositoryImpl(
    private val database: AppDatabase,
    private val api: ScheduleApi,
    private val session: SessionStore,
    private val settingsRepository: SettingsRepository,
) : ScheduleRepository {

    private val lessonDao = database.lessonDao()
    private val labDao = database.labDao()
    private val mutex = Mutex()
    private val syncState = MutableStateFlow(session.scheduleSyncAt)

    override val lastSyncAt: StateFlow<Long?> = syncState.asStateFlow()

    override val isConfigured: Boolean
        get() = settingsRepository.settings.value.scheduleGroup.isNotBlank()

    override fun observeUpcomingLessons(): Flow<List<Lesson>> =
        lessonDao.observeFrom(LocalDate.now().toEpochDay()).map { list -> list.map { it.toDomain() } }

    override fun observeLessonSubjects(): Flow<List<String>> = lessonDao.observeSubjectNames()

    override fun observeQueuedWorks(): Flow<List<QueuedWork>> =
        labDao.observeQueuedForDefense().map { rows ->
            rows.map { row ->
                QueuedWork(
                    labId = row.lab.id,
                    subjectId = row.lab.subjectId,
                    subjectTitle = row.subjectTitle,
                    scheduleTitle = row.scheduleTitle,
                    type = row.lab.type,
                    number = row.lab.number,
                    title = row.lab.title,
                )
            }
        }

    override suspend fun refresh(silent: Boolean): ScheduleRefreshResult = mutex.withLock {
        val settings = settingsRepository.settings.value
        if (settings.scheduleGroup.isBlank()) return@withLock ScheduleRefreshResult(emptyList(), emptyList())

        val html = api.fetch(settings.scheduleUrl)
        val parsed = ScheduleParser.parse(html, settings.scheduleGroup, settings.scheduleSubgroup.value)
        val byDate = parsed.lessons.groupBy { it.date }
        val newDates = mutableListOf<LocalDate>()
        val changedDates = mutableListOf<LocalDate>()
        val today = LocalDate.now()

        database.withTransaction {
            parsed.dates.forEach { date ->
                val day = date.toEpochDay()
                val fresh = byDate[date].orEmpty().map { it.toEntity() }
                val stored = lessonDao.getForDay(day)
                if (stored.map { it.signature() } == fresh.map { it.signature() }) return@forEach
                lessonDao.deleteDay(day)
                if (fresh.isNotEmpty()) lessonDao.insertAll(fresh)
                if (!silent && !date.isBefore(today)) {
                    if (stored.isEmpty()) newDates += date else changedDates += date
                }
            }
            lessonDao.deleteBefore(today.minusDays(KEEP_DAYS).toEpochDay())
        }

        val now = System.currentTimeMillis()
        session.scheduleSyncAt = now
        syncState.value = now
        ScheduleRefreshResult(newDates = newDates, changedDates = changedDates)
    }

    override suspend fun fetchGroups(url: String): List<String> {
        val html = api.fetch(url)
        return ScheduleParser.parse(html, group = "", subgroup = 0).groups
    }

    override suspend fun clear() {
        mutex.withLock {
            lessonDao.deleteAll()
            session.scheduleSyncAt = null
            syncState.value = null
        }
    }

    override suspend fun setDefendQueued(labId: Long, queued: Boolean) {
        labDao.setDefendQueued(labId, queued)
    }

    private companion object {
        const val KEEP_DAYS = 30L
    }
}

private fun ParsedLesson.toEntity(): LessonEntity = LessonEntity(
    date = date.toEpochDay(),
    number = number,
    startMinutes = start?.let { it.hour * 60 + it.minute },
    endMinutes = end?.let { it.hour * 60 + it.minute },
    subject = subject,
    teacher = teacher,
    room = room,
)

private fun LessonEntity.signature(): String =
    listOf(number, startMinutes, endMinutes, subject, teacher, room).joinToString("|")

private fun LessonEntity.toDomain(): Lesson = Lesson(
    id = id,
    date = LocalDate.ofEpochDay(date),
    number = number,
    start = startMinutes?.let { LocalTime.of(it / 60, it % 60) },
    end = endMinutes?.let { LocalTime.of(it / 60, it % 60) },
    subject = subject,
    teacher = teacher,
    room = room,
)
