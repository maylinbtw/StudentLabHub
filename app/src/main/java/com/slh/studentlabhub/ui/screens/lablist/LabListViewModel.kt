package com.slh.studentlabhub.ui.screens.lablist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.CourseTasks
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.slh.studentlabhub.data.schedule.DefenseQueue
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.repository.ScheduleRepository

data class LabListUiState(
    val isLoading: Boolean = true,
    val isMissing: Boolean = false,
    val details: SubjectDetails? = null,
    val course: CourseTasks? = null,
    val now: Long = System.currentTimeMillis(),
    val nextLesson: Lesson? = null,
    val queuedForNext: List<QueuedWork> = emptyList(),
    val scheduleConfigured: Boolean = false,
    val scheduleSubjects: List<String> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class LabListViewModel(
    private val subjectId: Long,
    private val repository: LabRepository,
    private val classroomRepository: ClassroomRepository,
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {

    private val details = repository.observeSubjectDetails(subjectId)

    private val course = details
        .map { it?.summary?.classroomCourseId }
        .distinctUntilChanged()
        .flatMapLatest { courseId ->
            if (courseId == null) flowOf(null) else classroomRepository.observeCourseTasks(courseId)
        }

    val uiState: StateFlow<LabListUiState> = combine(
        details,
        course,
        scheduleRepository.observeUpcomingLessons(),
        scheduleRepository.observeQueuedWorks(),
        scheduleRepository.observeLessonSubjects(),
    ) { details, course, lessons, queued, lessonSubjects ->
        val now = System.currentTimeMillis()
        val summary = details?.summary
        val next = summary?.let { DefenseQueue.nextLesson(lessons, it.title, it.scheduleTitle, now) }
        val assigned = DefenseQueue.assign(lessons, queued, now)
        LabListUiState(
            isLoading = false,
            isMissing = details == null,
            details = details,
            course = course,
            now = now,
            nextLesson = next,
            queuedForNext = next?.let { assigned[it.id] }.orEmpty().filter { it.subjectId == summary?.id },
            scheduleConfigured = scheduleRepository.isConfigured,
            scheduleSubjects = lessonSubjects,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LabListUiState(),
    )

    fun addWork(type: WorkType) {
        viewModelScope.launch {
            repository.addWork(subjectId, type)
        }
    }

    fun updateSubject(title: String, semester: Int, scheduleTitle: String?) {
        viewModelScope.launch {
            repository.updateSubject(
                subjectId = subjectId,
                title = title.trim(),
                semester = semester,
                scheduleTitle = scheduleTitle?.takeIf { it.isNotBlank() },
            )
        }
    }

    fun deleteSubject() {
        val courseId = uiState.value.details?.summary?.classroomCourseId
        courseId?.let { classroomRepository.setCourseArchived(it, archived = true) }
        viewModelScope.launch {
            repository.deleteSubject(subjectId)
        }
    }

    fun setArchived(archived: Boolean) {
        val courseId = uiState.value.details?.summary?.classroomCourseId
        viewModelScope.launch {
            repository.setSubjectArchived(subjectId, archived)
            courseId?.let { classroomRepository.setCourseArchived(it, archived) }
        }
    }

    fun setLabStatus(lab: Lab, status: LabStatus) {
        viewModelScope.launch {
            repository.updateLab(
                lab.copy(
                    status = status,
                    needsRedo = lab.needsRedo && status == LabStatus.NOT_DONE,
                    defendQueued = lab.defendQueued && status != LabStatus.DEFENDED,
                ),
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                val savedStateHandle = createSavedStateHandle()
                val subjectId = checkNotNull(savedStateHandle.get<Long>(Routes.ARG_SUBJECT_ID)) {
                    "subjectId is required"
                }
                LabListViewModel(
                    subjectId = subjectId,
                    repository = app.container.labRepository,
                    classroomRepository = app.container.classroomRepository,
                    scheduleRepository = app.container.scheduleRepository,
                )
            }
        }
    }
}
