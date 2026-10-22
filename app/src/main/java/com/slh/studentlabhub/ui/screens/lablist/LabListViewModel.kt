package com.slh.studentlabhub.ui.screens.lablist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LabListUiState(
    val isLoading: Boolean = true,
    val isMissing: Boolean = false,
    val details: SubjectDetails? = null,
)

class LabListViewModel(
    private val subjectId: Long,
    private val repository: LabRepository,
) : ViewModel() {

    val uiState: StateFlow<LabListUiState> = repository.observeSubjectDetails(subjectId)
        .map { details ->
            LabListUiState(
                isLoading = false,
                isMissing = details == null,
                details = details,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LabListUiState(),
        )

    fun addWork(type: WorkType) {
        viewModelScope.launch {
            repository.addWork(subjectId, type)
        }
    }

    fun updateSubject(title: String, semester: Int) {
        viewModelScope.launch {
            repository.updateSubject(
                subjectId = subjectId,
                title = title.trim(),
                semester = semester,
            )
        }
    }

    fun deleteSubject() {
        viewModelScope.launch {
            repository.deleteSubject(subjectId)
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
                )
            }
        }
    }
}