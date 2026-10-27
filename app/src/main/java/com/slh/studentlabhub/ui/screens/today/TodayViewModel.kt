package com.slh.studentlabhub.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.LabDeadline
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.ui.auth.toUserMessage
import com.slh.studentlabhub.ui.components.shortPrefix
import com.slh.studentlabhub.ui.components.toLocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import androidx.annotation.StringRes
import com.slh.studentlabhub.data.schedule.DefenseQueue
import com.slh.studentlabhub.data.schedule.SubjectMatcher
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.repository.ScheduleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

enum class AgendaGroup(@StringRes val titleRes: Int) {
    REDO(R.string.group_redo),
    OVERDUE(R.string.group_overdue),
    TODAY(R.string.group_today),
    TOMORROW(R.string.group_tomorrow),
    WEEK(R.string.group_week),
    LATER(R.string.group_later),
    ;

    val title: String
        get() = Strings.get(titleRes)
}

data class AgendaRow(
    val labId: Long,
    val title: String,
    val subjectTitle: String,
    val dueAt: Long?,
    val group: AgendaGroup,
)

data class AgendaSection(
    val group: AgendaGroup,
    val rows: List<AgendaRow>,
)

data class TodayUiState(
    val isLoading: Boolean = true,
    val now: Long = System.currentTimeMillis(),
    val today: LocalDate = LocalDate.now(),
    val sections: List<AgendaSection> = emptyList(),
    val calendarRows: List<AgendaRow> = emptyList(),
    val overdueCount: Int = 0,
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val redoCount: Int = 0,
    val hasSubjects: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
    val schedule: ScheduleBlock = ScheduleBlock(),
)

data class ScheduleBlock(
    val isConfigured: Boolean = false,
    val date: LocalDate? = null,
    val lessons: List<Lesson> = emptyList(),
    val currentLessonId: Long? = null,
    val defense: Map<Long, List<QueuedWork>> = emptyMap(),
    val lessonSubjectIds: Map<Long, Long> = emptyMap(),
    val hasMoreDays: Boolean = false,
    val syncAt: Long? = null,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

private data class ScheduleSyncState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

private data class SyncState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
)

class TodayViewModel(
    private val classroomRepository: ClassroomRepository,
    labRepository: LabRepository,
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {

    private val syncFlow = MutableStateFlow(SyncState(lastSyncAt = classroomRepository.lastSyncAt))

    private val scheduleSync = MutableStateFlow(ScheduleSyncState())

    private val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(60_000)
        }
    }

    private val subjectsFlow = labRepository.observeSubjects()

    private val scheduleFlow = combine(
        combine(
            scheduleRepository.observeUpcomingLessons(),
            scheduleRepository.observeQueuedWorks(),
            subjectsFlow,
        ) { lessons, queued, subjects -> Triple(lessons, queued, subjects) },
        scheduleRepository.lastSyncAt,
        scheduleSync,
        ticker,
    ) { (lessons, queued, subjects), syncAt, syncState, now ->
        buildSchedule(lessons, queued, subjects, syncAt, syncState, now)
    }

    val uiState: StateFlow<TodayUiState> = combine(
        labRepository.observePendingDeadlines(),
        subjectsFlow,
        syncFlow,
        scheduleFlow,
    ) { works, subjects, syncState, schedule ->
        buildState(works, subjects.isNotEmpty(), syncState).copy(schedule = schedule)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodayUiState(),
    )

    init {
        val last = scheduleRepository.lastSyncAt.value ?: 0L
        if (System.currentTimeMillis() - last > AUTO_REFRESH_MS) {
            refreshSchedule()
        }
    }

    fun onSyncStarted() {
        syncFlow.update { it.copy(isSyncing = true, errorMessage = null) }
        refreshSchedule()
    }

    fun refreshSchedule() {
        if (!scheduleRepository.isConfigured || scheduleSync.value.isSyncing) return
        scheduleSync.value = ScheduleSyncState(isSyncing = true)
        viewModelScope.launch {
            scheduleSync.value = try {
                scheduleRepository.refresh()
                ScheduleSyncState()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                ScheduleSyncState(errorMessage = Strings.get(R.string.schedule_load_failed))
            }
        }
    }

    private fun buildSchedule(
        lessons: List<Lesson>,
        queued: List<QueuedWork>,
        subjects: List<SubjectSummary>,
        syncAt: Long?,
        syncState: ScheduleSyncState,
        now: Long,
    ): ScheduleBlock {
        val upcoming = lessons.filter { it.endMillis() > now }
        val date = upcoming.minOfOrNull { it.date }
        val dayLessons = if (date == null) emptyList() else lessons.filter { it.date == date }
        val current = dayLessons.firstOrNull { now >= it.startMillis() && now < it.endMillis() }
        val subjectIds = dayLessons.mapNotNull { lesson ->
            subjects.firstOrNull { SubjectMatcher.matches(it.title, it.scheduleTitle, lesson.subject) }
                ?.let { lesson.id to it.id }
        }.toMap()
        return ScheduleBlock(
            isConfigured = scheduleRepository.isConfigured,
            date = date,
            lessons = dayLessons,
            currentLessonId = current?.id,
            defense = DefenseQueue.assign(lessons, queued, now),
            lessonSubjectIds = subjectIds,
            hasMoreDays = upcoming.any { it.date != date },
            syncAt = syncAt,
            isSyncing = syncState.isSyncing,
            errorMessage = syncState.errorMessage,
        )
    }

    fun onAuthorizationFailed(message: String) {
        syncFlow.update { it.copy(isSyncing = false, errorMessage = message) }
    }

    fun sync(accessToken: String) {
        viewModelScope.launch {
            try {
                classroomRepository.sync(accessToken)
                syncFlow.update {
                    it.copy(isSyncing = false, errorMessage = null, lastSyncAt = classroomRepository.lastSyncAt)
                }
            } catch (error: Exception) {
                syncFlow.update { it.copy(isSyncing = false, errorMessage = error.toUserMessage()) }
            }
        }
    }

    private fun buildState(
        works: List<LabDeadline>,
        hasSubjects: Boolean,
        syncState: SyncState,
    ): TodayUiState {
        val now = System.currentTimeMillis()
        val today = now.toLocalDate()
        val endOfWeek = today.with(DayOfWeek.SUNDAY)

        fun groupOf(work: LabDeadline, limitOverdue: Boolean): AgendaGroup? {
            val due = work.deadline
            return when {
                work.needsRedo -> AgendaGroup.REDO
                due == null -> null
                else -> {
                    val date = due.toLocalDate()
                    val days = ChronoUnit.DAYS.between(today, date)
                    when {
                        limitOverdue && due < now && days < -OVERDUE_WINDOW_DAYS -> null
                        due < now -> AgendaGroup.OVERDUE
                        days == 0L -> AgendaGroup.TODAY
                        days == 1L -> AgendaGroup.TOMORROW
                        !date.isAfter(endOfWeek) -> AgendaGroup.WEEK
                        else -> AgendaGroup.LATER
                    }
                }
            }
        }

        fun rowOf(work: LabDeadline, group: AgendaGroup) = AgendaRow(
            labId = work.labId,
            title = Strings.get(R.string.agenda_title, work.type.shortPrefix, work.number, work.title),
            subjectTitle = work.subjectTitle,
            dueAt = work.deadline,
            group = group,
        )

        val rows = works.mapNotNull { work ->
            groupOf(work, limitOverdue = true)?.let { rowOf(work, it) }
        }

        val calendarRows = works
            .filter { it.deadline != null }
            .mapNotNull { work -> groupOf(work, limitOverdue = false)?.let { rowOf(work, it) } }
            .sortedBy { it.dueAt }

        val sections = AgendaGroup.entries.mapNotNull { group ->
            val inGroup = rows.filter { it.group == group }.sortedBy { it.dueAt ?: Long.MAX_VALUE }
            if (inGroup.isEmpty()) null else AgendaSection(group, inGroup)
        }

        return TodayUiState(
            isLoading = false,
            now = now,
            today = today,
            sections = sections,
            calendarRows = calendarRows,
            overdueCount = rows.count { it.group == AgendaGroup.OVERDUE },
            todayCount = rows.count { it.group == AgendaGroup.TODAY },
            weekCount = rows.count { it.group == AgendaGroup.TOMORROW || it.group == AgendaGroup.WEEK },
            redoCount = rows.count { it.group == AgendaGroup.REDO },
            hasSubjects = hasSubjects,
            isSyncing = syncState.isSyncing,
            errorMessage = syncState.errorMessage,
            lastSyncAt = syncState.lastSyncAt,
        )
    }

    companion object {
        private const val OVERDUE_WINDOW_DAYS = 7L
        private const val AUTO_REFRESH_MS = 30L * 60 * 1000

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                TodayViewModel(
                    classroomRepository = app.container.classroomRepository,
                    labRepository = app.container.labRepository,
                    scheduleRepository = app.container.scheduleRepository,
                )
            }
        }
    }
}
