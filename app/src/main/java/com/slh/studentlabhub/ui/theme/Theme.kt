package com.slh.studentlabhub.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val SlhShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(16.dp),
)

@Composable
fun StudentLabHubTheme(content: @Composable () -> Unit) {
    val colorScheme = if (SlhPalette.isDark) {
        darkColorScheme(
            primary = SlhAccent,
            onPrimary = SlhOnAccent,
            secondary = SlhContent,
            onSecondary = SlhOnContent,
            background = SlhBackground,
            onBackground = SlhContent,
            surface = SlhSurface,
            onSurface = SlhContent,
            surfaceVariant = SlhSurfaceVariant,
            onSurfaceVariant = SlhTextSecondary,
            surfaceContainerHigh = SlhSurface,
            outline = SlhOutline,
            outlineVariant = SlhDotInactive,
            error = SlhDanger,
            onError = SlhContent,
        )
    } else {
        lightColorScheme(
            primary = SlhAccent,
            onPrimary = SlhOnAccent,
            secondary = SlhContent,
            onSecondary = SlhOnContent,
            background = SlhBackground,
            onBackground = SlhContent,
            surface = SlhSurface,
            onSurface = SlhContent,
            surfaceVariant = SlhSurfaceVariant,
            onSurfaceVariant = SlhTextSecondary,
            surfaceContainerHigh = SlhSurface,
            outline = SlhOutline,
            outlineVariant = SlhDotInactive,
            error = SlhDanger,
            onError = SlhOnContent,
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SlhTypography,
        shapes = SlhShapes,
        content = content,
    )
}
