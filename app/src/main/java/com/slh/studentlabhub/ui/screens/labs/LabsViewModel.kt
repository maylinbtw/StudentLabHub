package com.slh.studentlabhub.ui.screens.labs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SemesterSection(
    val semester: Int,
    val subjects: List<SubjectSummary>,
)

data class LabsUiState(
    val sections: List<SemesterSection> = emptyList(),
    val isLoading: Boolean = true,
)

class LabsViewModel(
    private val repository: LabRepository,
) : ViewModel() {

    val uiState: StateFlow<LabsUiState> = repository.observeSubjects()
        .map { subjects ->
            LabsUiState(
                sections = subjects
                    .groupBy { it.semester }
                    .entries
                    .sortedByDescending { it.key }
                    .map { SemesterSection(semester = it.key, subjects = it.value) },
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LabsUiState(),
        )

    fun addSubject(title: String, semester: Int, labsCount: Int) {
        viewModelScope.launch {
            repository.addSubject(
                title = title.trim(),
                semester = semester,
                labsCount = labsCount,
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                LabsViewModel(app.container.labRepository)
            }
        }
    }
}