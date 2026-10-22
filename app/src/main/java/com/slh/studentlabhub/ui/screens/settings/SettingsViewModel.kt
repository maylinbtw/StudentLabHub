package com.slh.studentlabhub.ui.screens.settings

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.data.backup.BackupException
import com.slh.studentlabhub.data.backup.BackupManager
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
    val backupMessage: String? = null,
    val isBackupBusy: Boolean = false,
)

private data class BackupState(
    val message: String? = null,
    val isBusy: Boolean = false,
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
    private val backupManager: BackupManager,
    private val contentResolver: ContentResolver,
    private val versionName: String,
) : ViewModel() {

    private val account = MutableStateFlow(AccountState())

    private val backup = MutableStateFlow(BackupState())

    private val accountAndBackup = combine(account, backup) { accountState, backupState ->
        accountState to backupState
    }

    private val counts = combine(
        referenceRepository.observeTldrCount(),
        referenceRepository.observeCustomCount(),
        labRepository.observeSubjects(),
    ) { tldr, custom, subjects -> Counts(tldr, custom, subjects.size) }

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        classroomRepository.redoThreshold,
        counts,
        accountAndBackup,
    ) { settings, threshold, counts, (accountState, backupState) ->
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
            backupMessage = backupState.message,
            isBackupBusy = backupState.isBusy,
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

    fun onLargeTextChanged(enabled: Boolean) = settingsRepository.update { it.copy(largeText = enabled) }

    fun exportBackup(uri: Uri) {
        runBackup {
            val summary = contentResolver.openOutputStream(uri)?.use { backupManager.export(it) }
                ?: throw BackupException("Не удалось создать файл")
            "Сохранено: ${summary.subjects} предм., ${summary.works} работ, ${summary.articles} статей"
        }
    }

    fun importBackup(uri: Uri) {
        runBackup {
            val summary = contentResolver.openInputStream(uri)?.use { backupManager.import(it) }
                ?: throw BackupException("Не удалось открыть файл")
            "Восстановлено: ${summary.subjects} предм., ${summary.works} работ, ${summary.articles} статей"
        }
    }

    private fun runBackup(block: suspend () -> String) {
        if (backup.value.isBusy) return
        backup.value = BackupState(isBusy = true)
        viewModelScope.launch {
            val message = try {
                block()
            } catch (error: BackupException) {
                error.message ?: "Ошибка резервной копии"
            } catch (error: Exception) {
                "Ошибка: ${error.message ?: "файл не подходит"}"
            }
            backup.value = BackupState(message = message)
        }
    }

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
                    backupManager = app.container.backupManager,
                    contentResolver = app.contentResolver,
                    versionName = versionName,
                )
            }
        }
    }
}
