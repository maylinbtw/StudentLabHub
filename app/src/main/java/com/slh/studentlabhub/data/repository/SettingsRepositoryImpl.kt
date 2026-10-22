package com.slh.studentlabhub.data.repository

import android.content.Context
import com.slh.studentlabhub.domain.model.AccentColor
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.BackgroundStyle
import com.slh.studentlabhub.domain.model.DotShape
import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val state = MutableStateFlow(load())

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override fun setAccent(value: AccentColor) {
        prefs.edit().putString(KEY_ACCENT, value.name).apply()
        state.update { it.copy(accent = value) }
    }

    override fun setBackground(value: BackgroundStyle) {
        prefs.edit().putString(KEY_BACKGROUND, value.name).apply()
        state.update { it.copy(background = value) }
    }

    override fun setDotShape(value: DotShape) {
        prefs.edit().putString(KEY_DOT_SHAPE, value.name).apply()
        state.update { it.copy(dotShape = value) }
    }

    override fun setStartScreen(value: StartScreen) {
        prefs.edit().putString(KEY_START_SCREEN, value.name).apply()
        state.update { it.copy(startScreen = value) }
    }

    private fun load(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            accent = readEnum(KEY_ACCENT, defaults.accent),
            background = readEnum(KEY_BACKGROUND, defaults.background),
            dotShape = readEnum(KEY_DOT_SHAPE, defaults.dotShape),
            startScreen = readEnum(KEY_START_SCREEN, defaults.startScreen),
        )
    }

    private inline fun <reified T : Enum<T>> readEnum(key: String, default: T): T {
        val name = prefs.getString(key, null) ?: return default
        return enumValues<T>().firstOrNull { it.name == name } ?: default
    }

    private companion object {
        const val PREFS_NAME = "slh_settings"
        const val KEY_ACCENT = "accent"
        const val KEY_BACKGROUND = "background"
        const val KEY_DOT_SHAPE = "dot_shape"
        const val KEY_START_SCREEN = "start_screen"
    }
}
