package com.slh.studentlabhub.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
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
    val colorScheme = darkColorScheme(
        primary = SlhAccent,
        onPrimary = SlhOnAccent,
        secondary = SlhWhite,
        onSecondary = SlhInk,
        background = SlhBackground,
        onBackground = SlhWhite,
        surface = SlhSurface,
        onSurface = SlhWhite,
        surfaceVariant = SlhSurfaceVariant,
        onSurfaceVariant = SlhTextSecondary,
        outline = SlhOutline,
        outlineVariant = SlhDotInactive,
        error = SlhDanger,
        onError = SlhWhite,
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SlhTypography,
        shapes = SlhShapes,
        content = content,
    )
}
