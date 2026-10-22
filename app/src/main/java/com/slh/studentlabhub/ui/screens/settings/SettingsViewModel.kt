package com.slh.studentlabhub.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.AccentColor
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.BackgroundStyle
import com.slh.studentlabhub.domain.model.DotShape
import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.ReferenceRepository
import com.slh.studentlabhub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val email: String? = null,
    val lastSyncAt: Long? = null,
    val redoThreshold: Int = 1,
    val tldrCount: Int = 0,
    val customCount: Int = 0,
    val versionName: String = "",
    val isSigningOut: Boolean = false,
    val isSignedOut: Boolean = false,
)

private data class AccountState(
    val isSigningOut: Boolean = false,
    val isSignedOut: Boolean = false,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val classroomRepository: ClassroomRepository,
    private val referenceRepository: ReferenceRepository,
    private val versionName: String,
) : ViewModel() {

    private val account = MutableStateFlow(AccountState())

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        classroomRepository.redoThreshold,
        referenceRepository.observeTldrCount(),
        referenceRepository.observeCustomCount(),
        account,
    ) { settings, threshold, tldrCount, customCount, accountState ->
        SettingsUiState(
            settings = settings,
            email = classroomRepository.signedInEmail,
            lastSyncAt = classroomRepository.lastSyncAt,
            redoThreshold = threshold,
            tldrCount = tldrCount,
            customCount = customCount,
            versionName = versionName,
            isSigningOut = accountState.isSigningOut,
            isSignedOut = accountState.isSignedOut,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(
            settings = settingsRepository.settings.value,
            email = classroomRepository.signedInEmail,
            lastSyncAt = classroomRepository.lastSyncAt,
            redoThreshold = classroomRepository.redoThreshold.value,
            versionName = versionName,
        ),
    )

    fun onAccentSelected(value: AccentColor) = settingsRepository.setAccent(value)

    fun onBackgroundSelected(value: BackgroundStyle) = settingsRepository.setBackground(value)

    fun onDotShapeSelected(value: DotShape) = settingsRepository.setDotShape(value)

    fun onStartScreenSelected(value: StartScreen) = settingsRepository.setStartScreen(value)

    fun onRedoThresholdSelected(value: Int) = classroomRepository.setRedoThreshold(value)

    fun clearTldrDownloads() {
        viewModelScope.launch {
            referenceRepository.clearTldrDownloads()
        }
    }

    fun signOut() {
        if (account.value.isSigningOut) return
        account.update { it.copy(isSigningOut = true) }
        viewModelScope.launch {
            runCatching { classroomRepository.signOut() }
            account.value = AccountState(isSigningOut = false, isSignedOut = true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                val versionName = runCatching {
                    app.packageManager.getPackageInfo(app.packageName, 0).versionName
                }.getOrNull().orEmpty()
                SettingsViewModel(
                    settingsRepository = app.container.settingsRepository,
                    classroomRepository = app.container.classroomRepository,
                    referenceRepository = app.container.referenceRepository,
                    versionName = versionName,
                )
            }
        }
    }
}
