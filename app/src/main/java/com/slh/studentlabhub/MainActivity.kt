package com.slh.studentlabhub

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.ThemeMode
import com.slh.studentlabhub.navigation.SlhApp
import com.slh.studentlabhub.ui.theme.SlhPalette
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val settingsRepository = (application as SlhApplication).container.settingsRepository
        applyAppearance(settingsRepository.settings.value)
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            settingsRepository.settings.collect { settings ->
                applyAppearance(settings)
            }
        }

        setContent {
            StudentLabHubTheme {
                SlhApp()
            }
        }
    }

    private fun applyAppearance(settings: AppSettings) {
        val dark = when (settings.themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemDark()
        }
        SlhPalette.apply(settings, dark)
        val barStyle = if (dark) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
        enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
    }

    private fun isSystemDark(): Boolean =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}
