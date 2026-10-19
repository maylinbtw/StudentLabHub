package com.slh.studentlabhub.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val SlhColorScheme = darkColorScheme(
    primary = SlhRed,
    onPrimary = SlhWhite,
    secondary = SlhWhite,
    onSecondary = SlhBlack,
    background = SlhBlack,
    onBackground = SlhWhite,
    surface = SlhSurface,
    onSurface = SlhWhite,
    surfaceVariant = SlhSurfaceVariant,
    onSurfaceVariant = SlhTextSecondary,
    outline = SlhOutline,
    outlineVariant = SlhDotInactive,
    error = SlhRed,
    onError = SlhWhite,
)

val SlhShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(16.dp),
)

@Composable
fun StudentLabHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SlhColorScheme,
        typography = SlhTypography,
        shapes = SlhShapes,
        content = content,
    )
}