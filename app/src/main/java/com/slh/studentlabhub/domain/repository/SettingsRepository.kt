package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>

    var notificationPromptShown: Boolean

    fun update(transform: (AppSettings) -> AppSettings)
}
