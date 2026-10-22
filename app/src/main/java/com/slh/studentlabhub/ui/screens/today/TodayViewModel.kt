package com.slh.studentlabhub.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.AgendaItem
import com.slh.studentlabhub.domain.model.AgendaSource
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.LabDeadline
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.TaskState
import com.slh.studentlabhub.domain.model.UpcomingTask
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

data class WeekDay(
    val date: LocalDate,
    val count: Int,
    val hasRedo: Boolean,
)

data class TodayUiState(
    val isLoading: Boolean = true,
    val now: Long = System.currentTimeMillis(),
    val today: LocalDate = LocalDate.now(),
    val weekOffset: Int = 0,
    val week: List<WeekDay> = emptyList(),
    val selectedDay: LocalDate? = null,
    val dayItems: List<AgendaItem> = emptyList(),
    val redo: List<AgendaItem> = emptyList(),
    val upcoming: List<AgendaItem> = emptyList(),
    val overallAverage: Double? = null,
    val totalWorks: Int = 0,
    val doneWorks: Int = 0,
    val defendedWorks: Int = 0,
    val subjectsCount: Int = 0,
    val coursesCount: Int = 0,
    val unlinkedCourses: Int = 0,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
) {
    val isEmptyStart: Boolean
        get() = subjectsCount == 0 && coursesCount == 0
}

private data class LocalState(
    val weekOffset: Int = 0,
    val selectedDay: LocalDate? = null,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
)

class TodayViewModel(
    private val classroomRepository: ClassroomRepository,
    labRepository: LabRepository,
) : ViewModel() {

    private val local = MutableStateFlow(LocalState(lastSyncAt = classroomRepository.lastSyncAt))

    val uiState: StateFlow<TodayUiState> = combine(
        classroomRepository.observeUpcomingTasks(),
        labRepository.observePendingDeadlines(),
        labRepository.observeSubjects(),
        classroomRepository.observeCourses(),
        local,
    ) { tasks, labDeadlines, subjects, courses, state ->
        buildState(tasks, labDeadlines, subjects, courses, state)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodayUiState(),
    )

    fun previousWeek() {
        local.update { it.copy(weekOffset = it.weekOffset - 1, selectedDay = null) }
    }

    fun nextWeek() {
        local.update { it.copy(weekOffset = it.weekOffset + 1, selectedDay = null) }
    }

    fun onDayClick(day: LocalDate) {
        local.update { it.copy(selectedDay = if (it.selectedDay == day) null else day) }
    }

    fun onSyncStarted() {
        local.update { it.copy(isSyncing = true, errorMessage = null) }
    }

    fun onAuthorizationFailed(message: String) {
        local.update { it.copy(isSyncing = false, errorMessage = message) }
    }

    fun sync(accessToken: String) {
        viewModelScope.launch {
            try {
                classroomRepository.sync(accessToken)
                local.update {
                    it.copy(
                        isSyncing = false,
                        errorMessage = null,
                        lastSyncAt = classroomRepository.lastSyncAt,
                    )
                }
            } catch (error: Exception) {
                local.update { it.copy(isSyncing = false, errorMessage = error.toUserMessage()) }
            }
        }
    }

    private fun buildState(
        tasks: List<UpcomingTask>,
        labDeadlines: List<LabDeadline>,
        subjects: List<SubjectSummary>,
        courses: List<CourseOverview>,
        state: LocalState,
    ): TodayUiState {
        val now = System.currentTimeMillis()
        val today = now.toLocalDate()
        val subjectByCourse = subjects
            .filter { it.classroomCourseId != null }
            .associateBy { it.classroomCourseId }

        val classroomItems = tasks.map { upcoming ->
            val linked = subjectByCourse[upcoming.courseId]
            AgendaItem(
                key = "gc_${upcoming.courseId}_${upcoming.task.id}",
                title = upcoming.task.title,
                subjectTitle = linked?.title ?: upcoming.courseTitle,
                dueAt = upcoming.task.dueAt,
                isRedo = upcoming.task.state == TaskState.REDO,
                source = AgendaSource.CLASSROOM,
                subjectId = linked?.id,
                courseId = upcoming.courseId,
            )
        }
        val labItems = labDeadlines.map { lab ->
            AgendaItem(
                key = "lab_${lab.labId}",
                title = "${lab.type.shortPrefix} №${lab.number} · ${lab.title}",
                subjectTitle = lab.subjectTitle,
                dueAt = lab.deadline,
                isRedo = false,
                source = AgendaSource.LAB,
                subjectId = lab.subjectId,
                labId = lab.labId,
            )
        }
        val all = classroomItems + labItems
        val dated = all
            .filter { it.dueAt != null }
            .sortedBy { it.dueAt }

        val monday = today
            .with(DayOfWeek.MONDAY)
            .plusWeeks(state.weekOffset.toLong())
        val week = (0L..6L).map { shift ->
            val date = monday.plusDays(shift)
            val onDay = dated.filter { it.dueAt?.toLocalDate() == date }
            WeekDay(
                date = date,
                count = onDay.size,
                hasRedo = onDay.any { it.isRedo },
            )
        }
        val selected = state.selectedDay
        val dayItems = if (selected == null) {
            emptyList()
        } else {
            dated.filter { it.dueAt?.toLocalDate() == selected }
        }

        val averages = subjects.mapNotNull { it.averageScore }
        val linkedCourseIds = subjects.mapNotNull { it.classroomCourseId }.toSet()

        return TodayUiState(
            isLoading = false,
            now = now,
            today = today,
            weekOffset = state.weekOffset,
            week = week,
            selectedDay = selected,
            dayItems = dayItems,
            redo = all.filter { it.isRedo },
            upcoming = dated.filter { !it.isRedo },
            overallAverage = if (averages.isEmpty()) null else averages.average(),
            totalWorks = subjects.sumOf { it.totalLabs },
            doneWorks = subjects.sumOf { it.doneLabs },
            defendedWorks = subjects.sumOf { it.defendedLabs },
            subjectsCount = subjects.size,
            coursesCount = courses.count { !it.archived },
            unlinkedCourses = courses.count { it.id !in linkedCourseIds && !it.archived },
            isSyncing = state.isSyncing,
            errorMessage = state.errorMessage,
            lastSyncAt = state.lastSyncAt,
        )
    }

    companion object {
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
