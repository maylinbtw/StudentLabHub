package com.slh.studentlabhub.ui.screens.deadlines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.ui.auth.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeadlinesUiState(
    val isLoading: Boolean = true,
    val courses: List<CourseOverview> = emptyList(),
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
)

private data class SyncState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncAt: Long? = null,
)

class DeadlinesViewModel(
    private val repository: ClassroomRepository,
) : ViewModel() {

    private val syncState = MutableStateFlow(SyncState(lastSyncAt = repository.lastSyncAt))

    val uiState: StateFlow<DeadlinesUiState> = combine(
        repository.observeCourses(),
        syncState,
    ) { courses, sync ->
        DeadlinesUiState(
            isLoading = false,
            courses = courses,
            isSyncing = sync.isSyncing,
            errorMessage = sync.errorMessage,
            lastSyncAt = sync.lastSyncAt,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DeadlinesUiState(),
    )

    fun onSyncStarted() {
        syncState.update { it.copy(isSyncing = true, errorMessage = null) }
    }

    fun onAuthorizationFailed(message: String) {
        syncState.update { it.copy(isSyncing = false, errorMessage = message) }
    }

    fun sync(accessToken: String) {
        viewModelScope.launch {
            try {
                repository.sync(accessToken)
                syncState.update {
                    it.copy(isSyncing = false, errorMessage = null, lastSyncAt = repository.lastSyncAt)
                }
            } catch (error: Exception) {
                syncState.update {
                    it.copy(isSyncing = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                DeadlinesViewModel(app.container.classroomRepository)
            }
        }
    }
}