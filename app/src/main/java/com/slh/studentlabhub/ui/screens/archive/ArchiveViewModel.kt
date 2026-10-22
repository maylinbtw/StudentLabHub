package com.slh.studentlabhub.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ArchiveUiState(
    val isLoading: Boolean = true,
    val subjects: List<SubjectSummary> = emptyList(),
    val courses: List<CourseOverview> = emptyList(),
) {
    val isEmpty: Boolean
        get() = subjects.isEmpty() && courses.isEmpty()
}

class ArchiveViewModel(
    private val labRepository: LabRepository,
    private val classroomRepository: ClassroomRepository,
) : ViewModel() {

    val uiState: StateFlow<ArchiveUiState> = combine(
        labRepository.observeArchivedSubjects(),
        labRepository.observeSubjects(),
        classroomRepository.observeCourses(),
    ) { archivedSubjects, activeSubjects, courses ->
        val linkedIds = (archivedSubjects + activeSubjects).mapNotNull { it.classroomCourseId }.toSet()
        ArchiveUiState(
            isLoading = false,
            subjects = archivedSubjects.sortedWith(
                compareByDescending<SubjectSummary> { it.semester }.thenBy { it.title },
            ),
            courses = courses.filter { it.archived && it.id !in linkedIds },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ArchiveUiState(),
    )

    fun restoreSubject(subject: SubjectSummary) {
        viewModelScope.launch {
            labRepository.setSubjectArchived(subject.id, archived = false)
        }
        subject.classroomCourseId?.let { classroomRepository.setCourseArchived(it, archived = false) }
    }

    fun restoreCourse(course: CourseOverview) {
        classroomRepository.setCourseArchived(course.id, archived = false)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                ArchiveViewModel(
                    labRepository = app.container.labRepository,
                    classroomRepository = app.container.classroomRepository,
                )
            }
        }
    }
}
