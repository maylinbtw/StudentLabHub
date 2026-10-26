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

enum class AgendaGroup(val title: String) {
    REDO("НА ДОРАБОТКУ"),
    OVERDUE("ПРОСРОЧЕНО"),
    TODAY("СЕГОДНЯ"),
    TOMORROW("ЗАВТРА"),
    WEEK("НА ЭТОЙ НЕДЕЛЕ"),
    LATER("ПОЗЖЕ"),
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
    val overdueCount: Int = 0,
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val redoCount: Int = 0,
    val hasSubjects: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
)

private data class SyncState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
)

class TodayViewModel(
    private val classroomRepository: ClassroomRepository,
    labRepository: LabRepository,
) : ViewModel() {

    private val syncFlow = MutableStateFlow(SyncState(lastSyncAt = classroomRepository.lastSyncAt))

    val uiState: StateFlow<TodayUiState> = combine(
        labRepository.observePendingDeadlines(),
        labRepository.observeSubjects(),
        syncFlow,
    ) { works, subjects, syncState ->
        buildState(works, subjects.isNotEmpty(), syncState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodayUiState(),
    )

    fun onSyncStarted() {
        syncFlow.update { it.copy(isSyncing = true, errorMessage = null) }
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

        val rows = works.mapNotNull { work ->
            val due = work.deadline
            val group = when {
                work.needsRedo -> AgendaGroup.REDO
                due == null -> null
                else -> {
                    val date = due.toLocalDate()
                    val days = ChronoUnit.DAYS.between(today, date)
                    when {
                        due < now && days < -OVERDUE_WINDOW_DAYS -> null
                        due < now -> AgendaGroup.OVERDUE
                        days == 0L -> AgendaGroup.TODAY
                        days == 1L -> AgendaGroup.TOMORROW
                        !date.isAfter(endOfWeek) -> AgendaGroup.WEEK
                        else -> AgendaGroup.LATER
                    }
                }
            } ?: return@mapNotNull null
            AgendaRow(
                labId = work.labId,
                title = "${work.type.shortPrefix} ${work.number} · ${work.title}",
                subjectTitle = work.subjectTitle,
                dueAt = due,
                group = group,
            )
        }

        val sections = AgendaGroup.entries.mapNotNull { group ->
            val inGroup = rows.filter { it.group == group }.sortedBy { it.dueAt ?: Long.MAX_VALUE }
            if (inGroup.isEmpty()) null else AgendaSection(group, inGroup)
        }

        return TodayUiState(
            isLoading = false,
            now = now,
            today = today,
            sections = sections,
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

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                TodayViewModel(
                    classroomRepository = app.container.classroomRepository,
                    labRepository = app.container.labRepository,
                )
            }
        }
    }
}
