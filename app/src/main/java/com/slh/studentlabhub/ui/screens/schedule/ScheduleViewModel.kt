package com.slh.studentlabhub.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.R
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.data.schedule.DefenseQueue
import com.slh.studentlabhub.data.schedule.SubjectMatcher
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.model.ScheduleSubgroup
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.domain.repository.ScheduleRepository
import com.slh.studentlabhub.domain.repository.SettingsRepository
import com.slh.studentlabhub.i18n.Strings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ScheduleDay(
    val date: LocalDate,
    val lessons: List<Lesson>,
)

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val isConfigured: Boolean = false,
    val title: String = "",
    val days: List<ScheduleDay> = emptyList(),
    val defense: Map<Long, List<QueuedWork>> = emptyMap(),
    val lessonSubjectIds: Map<Long, Long> = emptyMap(),
    val currentLessonId: Long? = null,
    val syncAt: Long? = null,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

private data class RefreshState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    labRepository: LabRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    private val refreshState = MutableStateFlow(RefreshState())

    val uiState: StateFlow<ScheduleUiState> = combine(
        scheduleRepository.observeUpcomingLessons(),
        scheduleRepository.observeQueuedWorks(),
        labRepository.observeSubjects(),
        combine(scheduleRepository.lastSyncAt, refreshState) { syncAt, refresh -> syncAt to refresh },
        settingsRepository.settings,
    ) { lessons, queued, subjects, (syncAt, refresh), settings ->
        val now = System.currentTimeMillis()
        val subgroup = when (settings.scheduleSubgroup) {
            ScheduleSubgroup.BOTH -> null
            ScheduleSubgroup.FIRST -> Strings.get(R.string.subgroup_n, 1)
            ScheduleSubgroup.SECOND -> Strings.get(R.string.subgroup_n, 2)
        }
        ScheduleUiState(
            isLoading = false,
            isConfigured = settings.scheduleGroup.isNotBlank(),
            title = listOfNotNull(settings.scheduleGroup.takeIf { it.isNotBlank() }, subgroup).joinToString(" · "),
            days = lessons.groupBy { it.date }.map { (date, list) -> ScheduleDay(date, list) },
            defense = DefenseQueue.assign(lessons, queued, now),
            lessonSubjectIds = lessons.mapNotNull { lesson ->
                subjects.firstOrNull { SubjectMatcher.matches(it.title, it.scheduleTitle, lesson.subject) }
                    ?.let { lesson.id to it.id }
            }.toMap(),
            currentLessonId = lessons.firstOrNull { now >= it.startMillis() && now < it.endMillis() }?.id,
            syncAt = syncAt,
            isSyncing = refresh.isSyncing,
            errorMessage = refresh.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScheduleUiState(),
    )

    fun refresh() {
        if (!scheduleRepository.isConfigured || refreshState.value.isSyncing) return
        refreshState.value = RefreshState(isSyncing = true)
        viewModelScope.launch {
            refreshState.value = try {
                scheduleRepository.refresh()
                RefreshState()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                RefreshState(errorMessage = Strings.get(R.string.schedule_load_failed))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                ScheduleViewModel(
                    scheduleRepository = app.container.scheduleRepository,
                    labRepository = app.container.labRepository,
                    settingsRepository = app.container.settingsRepository,
                )
            }
        }
    }
}
