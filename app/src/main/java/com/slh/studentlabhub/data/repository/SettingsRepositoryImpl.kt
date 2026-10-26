package com.slh.studentlabhub.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val state = MutableStateFlow(load())

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override var notificationPromptShown: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_PROMPT, false)
        set(value) {
            prefs.edit().putBoolean(KEY_NOTIFICATION_PROMPT, value).apply()
        }

    @Synchronized
    override fun update(transform: (AppSettings) -> AppSettings) {
        val updated = transform(state.value)
        if (updated == state.value) return
        save(updated)
        state.value = updated
    }

    private fun save(settings: AppSettings) {
        prefs.edit()
            .putString(KEY_ACCENT, settings.accent.name)
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putString(KEY_BACKGROUND, settings.background.name)
            .putString(KEY_DOT_SHAPE, settings.dotShape.name)
            .putString(KEY_START_SCREEN, settings.startScreen.name)
            .putBoolean(KEY_REMINDERS, settings.remindersEnabled)
            .putString(KEY_REMINDER_TIMING, settings.reminderTiming.name)
            .putBoolean(KEY_REDO_ALERTS, settings.redoAlerts)
            .putBoolean(KEY_BACKGROUND_SYNC, settings.backgroundSync)
            .putBoolean(KEY_LARGE_TEXT, settings.largeText)
            .putString(KEY_LANGUAGE, settings.language.name)
            .apply()
    }

    private fun load(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            accent = prefs.readEnum(KEY_ACCENT, defaults.accent),
            themeMode = prefs.readEnum(KEY_THEME_MODE, defaults.themeMode),
            background = prefs.readEnum(KEY_BACKGROUND, defaults.background),
            dotShape = prefs.readEnum(KEY_DOT_SHAPE, defaults.dotShape),
            startScreen = prefs.readEnum(KEY_START_SCREEN, defaults.startScreen),
            remindersEnabled = prefs.getBoolean(KEY_REMINDERS, defaults.remindersEnabled),
            reminderTiming = prefs.readEnum(KEY_REMINDER_TIMING, defaults.reminderTiming),
            redoAlerts = prefs.getBoolean(KEY_REDO_ALERTS, defaults.redoAlerts),
            backgroundSync = prefs.getBoolean(KEY_BACKGROUND_SYNC, defaults.backgroundSync),
            largeText = prefs.getBoolean(KEY_LARGE_TEXT, defaults.largeText),
            language = prefs.readEnum(KEY_LANGUAGE, defaults.language),
        )
    }

    private companion object {
        const val PREFS_NAME = "slh_settings"
        const val KEY_ACCENT = "accent"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_BACKGROUND = "background"
        const val KEY_DOT_SHAPE = "dot_shape"
        const val KEY_START_SCREEN = "start_screen"
        const val KEY_REMINDERS = "reminders_enabled"
        const val KEY_REMINDER_TIMING = "reminder_timing"
        const val KEY_REDO_ALERTS = "redo_alerts"
        const val KEY_BACKGROUND_SYNC = "background_sync"
        const val KEY_NOTIFICATION_PROMPT = "notification_prompt_shown"
        const val KEY_LARGE_TEXT = "large_text"
        const val KEY_LANGUAGE = "language"
    }
}

private inline fun <reified T : Enum<T>> SharedPreferences.readEnum(key: String, default: T): T {
    val name = getString(key, null) ?: return default
    return enumValues<T>().firstOrNull { it.name == name } ?: default
}
