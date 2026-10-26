package com.slh.studentlabhub.ui.screens.labdetails

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.data.local.LabPhotoStore
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LabDetailsUiState(
    val isLoading: Boolean = true,
    val isMissing: Boolean = false,
    val isFinished: Boolean = false,
    val isSaving: Boolean = false,
    val subjectTitle: String = "",
    val type: WorkType = WorkType.LAB,
    val number: Int = 0,
    val title: String = "",
    val status: LabStatus = LabStatus.NOT_DONE,
    val scoreInput: String = "",
    val notes: String = "",
    val deadline: Long? = null,
    val photos: List<String> = emptyList(),
    val isAddingPhotos: Boolean = false,
    val link: String? = null,
    val needsRedo: Boolean = false,
    val fromClassroom: Boolean = false,
) {
    val canSave: Boolean
        get() = !isLoading && !isSaving && title.isNotBlank()

    val hasScore: Boolean
        get() = true
}

class LabDetailsViewModel(
    private val labId: Long,
    private val repository: LabRepository,
    private val photoStore: LabPhotoStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LabDetailsUiState())
    val uiState: StateFlow<LabDetailsUiState> = _uiState.asStateFlow()

    private var original: Lab? = null

    init {
        viewModelScope.launch {
            val details = repository.getLabDetails(labId)
            if (details == null) {
                _uiState.update { it.copy(isLoading = false, isMissing = true) }
            } else {
                original = details.lab
                _uiState.value = LabDetailsUiState(
                    isLoading = false,
                    subjectTitle = details.subjectTitle,
                    type = details.lab.type,
                    number = details.lab.number,
                    title = details.lab.title,
                    status = details.lab.status,
                    scoreInput = details.lab.score?.toString().orEmpty(),
                    notes = details.lab.notes,
                    deadline = details.lab.deadline,
                    photos = photoStore.list(labId),
                    link = details.lab.link,
                    needsRedo = details.lab.needsRedo,
                    fromClassroom = details.lab.fromClassroom,
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value.take(80)) }
    }

    fun onStatusChange(status: LabStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun onScoreChange(input: String) {
        val digits = input.filter { it.isDigit() }.take(2)
        val value = digits.toIntOrNull()
        if (digits.isEmpty() || (value != null && value in 0..10)) {
            _uiState.update { it.copy(scoreInput = digits) }
        }
    }

    fun onDeadlineChange(value: Long?) {
        _uiState.update { it.copy(deadline = value) }
    }

    fun addPhotos(uris: List<Uri>) {
        if (uris.isEmpty()) return
        _uiState.update { it.copy(isAddingPhotos = true) }
        viewModelScope.launch {
            uris.forEach { uri -> runCatching { photoStore.add(labId, uri) } }
            _uiState.update { it.copy(isAddingPhotos = false, photos = photoStore.list(labId)) }
        }
    }

    fun deletePhoto(path: String) {
        viewModelScope.launch {
            photoStore.delete(path)
            _uiState.update { it.copy(photos = photoStore.list(labId)) }
        }
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value.take(2000)) }
    }

    fun save() {
        val lab = original ?: return
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.updateLab(
                lab.copy(
                    title = state.title.trim(),
                    status = state.status,
                    score = if (state.hasScore) state.scoreInput.toIntOrNull() else null,
                    notes = state.notes.trimEnd(),
                    deadline = state.deadline,
                    needsRedo = lab.needsRedo && state.status == LabStatus.NOT_DONE,
                ),
            )
            _uiState.update { it.copy(isSaving = false, isFinished = true) }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.deleteLab(labId)
            photoStore.deleteAll(labId)
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                val savedStateHandle = createSavedStateHandle()
                val labId = checkNotNull(savedStateHandle.get<Long>(Routes.ARG_LAB_ID)) {
                    "labId is required"
                }
                LabDetailsViewModel(
                    labId = labId,
                    repository = app.container.labRepository,
                    photoStore = app.container.labPhotoStore,
                )
            }
        }
    }
}