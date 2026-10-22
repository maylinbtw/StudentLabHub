package com.slh.studentlabhub.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.ui.auth.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: ClassroomRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(accountEmail = repository.signedInEmail))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun onAuthStarted() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    }

    fun onAuthFailed(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    fun onAuthorized(accessToken: String) {
        viewModelScope.launch {
            try {
                val email = repository.completeSignIn(accessToken)
                _uiState.update { it.copy(accountEmail = email) }
                runCatching { repository.sync(accessToken) }
                _isLoggedIn.value = true
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    fun switchAccount(onReady: () -> Unit) {
        viewModelScope.launch {
            repository.signOut()
            _uiState.value = LoginUiState(isLoading = true)
            onReady()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                LoginViewModel(app.container.classroomRepository)
            }
        }
    }
}