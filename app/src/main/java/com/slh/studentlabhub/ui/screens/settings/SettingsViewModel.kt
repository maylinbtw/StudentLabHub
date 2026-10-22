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
import com.slh.studentlabhub.domain.model.ReminderTiming
import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.domain.model.ThemeMode
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
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
    val subjectsCount: Int = 0,
    val versionName: String = "",
    val isSigningOut: Boolean = false,
    val isSignedOut: Boolean = false,
)

private data class Counts(
    val tldr: Int,
    val custom: Int,
    val subjects: Int,
)

private data class AccountState(
    val isSigningOut: Boolean = false,
    val isSignedOut: Boolean = false,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val classroomRepository: ClassroomRepository,
    private val referenceRepository: ReferenceRepository,
    private val labRepository: LabRepository,
    private val versionName: String,
) : ViewModel() {

    private val account = MutableStateFlow(AccountState())

    private val counts = combine(
        referenceRepository.observeTldrCount(),
        referenceRepository.observeCustomCount(),
        labRepository.observeSubjects(),
    ) { tldr, custom, subjects -> Counts(tldr, custom, subjects.size) }

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        classroomRepository.redoThreshold,
        counts,
        account,
    ) { settings, threshold, counts, accountState ->
        SettingsUiState(
            settings = settings,
            email = classroomRepository.signedInEmail,
            lastSyncAt = classroomRepository.lastSyncAt,
            redoThreshold = threshold,
            tldrCount = counts.tldr,
            customCount = counts.custom,
            subjectsCount = counts.subjects,
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
            subjectsCount = -1,
            versionName = versionName,
        ),
    )

    fun onAccentSelected(value: AccentColor) = settingsRepository.update { it.copy(accent = value) }

    fun onThemeModeSelected(value: ThemeMode) = settingsRepository.update { it.copy(themeMode = value) }

    fun onBackgroundSelected(value: BackgroundStyle) = settingsRepository.update { it.copy(background = value) }

    fun onDotShapeSelected(value: DotShape) = settingsRepository.update { it.copy(dotShape = value) }

    fun onStartScreenSelected(value: StartScreen) = settingsRepository.update { it.copy(startScreen = value) }

    fun onRemindersChanged(enabled: Boolean) = settingsRepository.update { it.copy(remindersEnabled = enabled) }

    fun onReminderTimingSelected(value: ReminderTiming) = settingsRepository.update { it.copy(reminderTiming = value) }

    fun onRedoAlertsChanged(enabled: Boolean) = settingsRepository.update { it.copy(redoAlerts = enabled) }

    fun onBackgroundSyncChanged(enabled: Boolean) = settingsRepository.update { it.copy(backgroundSync = enabled) }

    fun onRedoThresholdSelected(value: Int) = classroomRepository.setRedoThreshold(value)

    fun clearTldrDownloads() {
        viewModelScope.launch {
            referenceRepository.clearTldrDownloads()
        }
    }

    fun insertDemoData() {
        viewModelScope.launch {
            labRepository.insertDemoDataIfEmpty()
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
                    labRepository = app.container.labRepository,
                    versionName = versionName,
                )
            }
        }
    }
}
