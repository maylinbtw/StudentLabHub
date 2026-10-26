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
import com.slh.studentlabhub.ui.auth.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class SyncState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

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
    val archivedCount: Int = 0,
    val overallAverage: Double? = null,
    val totalWorks: Int = 0,
    val doneWorks: Int = 0,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean
        get() = groups.isEmpty()
}

class SubjectsViewModel(
    private val labRepository: LabRepository,
    private val classroomRepository: ClassroomRepository,
) : ViewModel() {

    private val syncFlow = MutableStateFlow(SyncState())

    val uiState: StateFlow<SubjectsUiState> = combine(
        labRepository.observeSubjects(),
        labRepository.observeArchivedSubjects(),
        classroomRepository.observeCourses(),
        syncFlow,
    ) { subjects, archivedSubjects, courses, syncState ->
        val coursesById = courses.associateBy { it.id }
        val averages = subjects.mapNotNull { it.averageScore }
        SubjectsUiState(
            isLoading = false,
            groups = subjects
                .map { SubjectItem(summary = it, course = it.classroomCourseId?.let(coursesById::get)) }
                .groupBy { it.summary.semester }
                .entries
                .sortedByDescending { it.key }
                .map { SemesterGroup(semester = it.key, subjects = it.value) },
            archivedCount = archivedSubjects.size,
            overallAverage = if (averages.isEmpty()) null else averages.average(),
            totalWorks = subjects.sumOf { it.totalLabs },
            doneWorks = subjects.sumOf { it.doneLabs },
            isSyncing = syncState.isSyncing,
            errorMessage = syncState.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubjectsUiState(),
    )

    fun onSyncStarted() {
        syncFlow.value = SyncState(isSyncing = true)
    }

    fun onAuthorizationFailed(message: String) {
        syncFlow.value = SyncState(errorMessage = message)
    }

    fun sync(accessToken: String) {
        viewModelScope.launch {
            syncFlow.value = try {
                classroomRepository.sync(accessToken)
                SyncState()
            } catch (error: Exception) {
                SyncState(errorMessage = error.toUserMessage())
            }
        }
    }

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

    fun archiveSemester(semester: Int) {
        viewModelScope.launch {
            val courseIds = labRepository.archiveSemester(semester)
            classroomRepository.setCoursesArchived(courseIds, archived = true)
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
