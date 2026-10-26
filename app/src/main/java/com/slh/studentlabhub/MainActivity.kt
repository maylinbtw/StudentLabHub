package com.slh.studentlabhub

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.slh.studentlabhub.domain.model.AppLanguage
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.ThemeMode
import com.slh.studentlabhub.i18n.AppLocale
import com.slh.studentlabhub.navigation.LaunchActions
import com.slh.studentlabhub.navigation.SlhApp
import com.slh.studentlabhub.ui.theme.SlhPalette
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import com.slh.studentlabhub.widget.DeadlinesWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var appliedLanguage: AppLanguage? = null

    override fun attachBaseContext(newBase: Context) {
        val app = newBase.applicationContext as SlhApplication
        val language = app.container.settingsRepository.settings.value.language
        appliedLanguage = language
        super.attachBaseContext(AppLocale.wrap(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val settingsRepository = (application as SlhApplication).container.settingsRepository
        applyAppearance(settingsRepository.settings.value)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            LaunchActions.submit(intent?.action)
        }

        lifecycleScope.launch {
            settingsRepository.settings.collect { settings ->
                applyAppearance(settings)
                if (appliedLanguage != null && settings.language != appliedLanguage) {
                    appliedLanguage = settings.language
                    recreate()
                }
            }
        }

        setContent {
            StudentLabHubTheme {
                SlhApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        LaunchActions.submit(intent.action)
    }

    override fun onStop() {
        super.onStop()
        val container = (application as SlhApplication).container
        container.applicationScope.launch {
            DeadlinesWidget.refresh(applicationContext)
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
