package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.AccentColor
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.BackgroundStyle
import com.slh.studentlabhub.domain.model.DotShape
import com.slh.studentlabhub.domain.model.StartScreen
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>

    fun setAccent(value: AccentColor)

    fun setBackground(value: BackgroundStyle)

    fun setDotShape(value: DotShape)

    fun setStartScreen(value: StartScreen)
}
