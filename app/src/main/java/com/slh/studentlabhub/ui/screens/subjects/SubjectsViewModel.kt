package com.slh.studentlabhub.ui.screens.subjects

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

data class SubjectItem(
    val summary: SubjectSummary,
    val course: CourseOverview?,
)

data class SemesterGroup(
    val semester: Int,
    val subjects: List<SubjectItem>,
)

data class SubjectsUiState(
    val isLoading: Boolean = true,
    val groups: List<SemesterGroup> = emptyList(),
    val unlinkedCourses: List<CourseOverview> = emptyList(),
    val now: Long = System.currentTimeMillis(),
) {
    val isEmpty: Boolean
        get() = groups.isEmpty() && unlinkedCourses.isEmpty()
}

class SubjectsViewModel(
    private val labRepository: LabRepository,
    classroomRepository: ClassroomRepository,
) : ViewModel() {

    val uiState: StateFlow<SubjectsUiState> = combine(
        labRepository.observeSubjects(),
        classroomRepository.observeCourses(),
    ) { subjects, courses ->
        val coursesById = courses.associateBy { it.id }
        val linkedIds = subjects.mapNotNull { it.classroomCourseId }.toSet()
        SubjectsUiState(
            isLoading = false,
            groups = subjects
                .map { SubjectItem(summary = it, course = it.classroomCourseId?.let(coursesById::get)) }
                .groupBy { it.summary.semester }
                .entries
                .sortedByDescending { it.key }
                .map { SemesterGroup(semester = it.key, subjects = it.value) },
            unlinkedCourses = courses.filter { it.id !in linkedIds },
            now = System.currentTimeMillis(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubjectsUiState(),
    )

    fun addSubject(title: String, semester: Int, labsCount: Int, practiceCount: Int) {
        viewModelScope.launch {
            labRepository.addSubject(
                title = title.trim(),
                semester = semester,
                labsCount = labsCount,
                practiceCount = practiceCount,
            )
        }
    }

    fun addCourse(course: CourseOverview) {
        viewModelScope.launch {
            labRepository.createSubjectForCourse(course.title, course.id)
        }
    }

    fun addAllCourses() {
        val courses = uiState.value.unlinkedCourses
        viewModelScope.launch {
            courses.forEach { course ->
                labRepository.createSubjectForCourse(course.title, course.id)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                SubjectsViewModel(
                    labRepository = app.container.labRepository,
                    classroomRepository = app.container.classroomRepository,
                )
            }
        }
    }
}
