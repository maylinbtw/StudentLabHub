package com.slh.studentlabhub.ui.screens.coursetasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.CourseTasks
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CourseTasksUiState(
    val isLoading: Boolean = true,
    val isMissing: Boolean = false,
    val course: CourseTasks? = null,
    val linkedSubject: SubjectSummary? = null,
    val subjects: List<SubjectSummary> = emptyList(),
    val now: Long = System.currentTimeMillis(),
)

class CourseTasksViewModel(
    private val courseId: String,
    classroomRepository: ClassroomRepository,
    private val labRepository: LabRepository,
) : ViewModel() {

    val uiState: StateFlow<CourseTasksUiState> = combine(
        classroomRepository.observeCourseTasks(courseId),
        labRepository.observeSubjects(),
    ) { course, subjects ->
        CourseTasksUiState(
            isLoading = false,
            isMissing = course == null,
            course = course,
            linkedSubject = subjects.firstOrNull { it.classroomCourseId == courseId },
            subjects = subjects,
            now = System.currentTimeMillis(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CourseTasksUiState(),
    )

    fun linkSubject(subjectId: Long) {
        viewModelScope.launch {
            labRepository.linkSubjectToCourse(subjectId, courseId)
        }
    }

    fun unlinkSubject() {
        viewModelScope.launch {
            labRepository.unlinkCourse(courseId)
        }
    }

    fun createLinkedSubject() {
        val title = uiState.value.course?.title ?: return
        viewModelScope.launch {
            labRepository.createSubjectForCourse(title, courseId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                val savedStateHandle = createSavedStateHandle()
                val courseId = checkNotNull(savedStateHandle.get<String>(Routes.ARG_COURSE_ID)) {
                    "courseId is required"
                }
                CourseTasksViewModel(
                    courseId = courseId,
                    classroomRepository = app.container.classroomRepository,
                    labRepository = app.container.labRepository,
                )
            }
        }
    }
}
