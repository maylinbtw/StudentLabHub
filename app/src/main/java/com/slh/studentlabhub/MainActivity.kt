package com.slh.studentlabhub

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.slh.studentlabhub.navigation.SlhApp
import com.slh.studentlabhub.ui.theme.SlhPalette
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        val settingsRepository = (application as SlhApplication).container.settingsRepository
        SlhPalette.apply(settingsRepository.settings.value)
        lifecycleScope.launch {
            settingsRepository.settings.collect { settings ->
                SlhPalette.apply(settings)
            }
        }

        setContent {
            StudentLabHubTheme {
                SlhApp()
            }
        }
    }
}
