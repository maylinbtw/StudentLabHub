package com.slh.studentlabhub.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.domain.model.AccentColor
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.BackgroundStyle
import com.slh.studentlabhub.domain.model.DotShape

val AccentColor.color: Color
    get() = when (this) {
        AccentColor.RED -> Color(0xFFFF3B30)
        AccentColor.ORANGE -> Color(0xFFFF8A1F)
        AccentColor.YELLOW -> Color(0xFFFFD60A)
        AccentColor.GREEN -> Color(0xFF32D74B)
        AccentColor.CYAN -> Color(0xFF40C8E0)
        AccentColor.BLUE -> Color(0xFF0A84FF)
        AccentColor.VIOLET -> Color(0xFFBF5AF2)
        AccentColor.PINK -> Color(0xFFFF375F)
    }

val AccentColor.onColor: Color
    get() = when (this) {
        AccentColor.ORANGE,
        AccentColor.YELLOW,
        AccentColor.GREEN,
        AccentColor.CYAN,
        -> SlhInk

        AccentColor.RED,
        AccentColor.BLUE,
        AccentColor.VIOLET,
        AccentColor.PINK,
        -> SlhWhite
    }

val AccentColor.label: String
    get() = when (this) {
        AccentColor.RED -> "Красный"
        AccentColor.ORANGE -> "Оранжевый"
        AccentColor.YELLOW -> "Жёлтый"
        AccentColor.GREEN -> "Зелёный"
        AccentColor.CYAN -> "Бирюзовый"
        AccentColor.BLUE -> "Синий"
        AccentColor.VIOLET -> "Фиолетовый"
        AccentColor.PINK -> "Розовый"
    }

private class BackgroundTones(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val dotInactive: Color,
)

private fun BackgroundStyle.tones(): BackgroundTones = when (this) {
    BackgroundStyle.AMOLED -> BackgroundTones(
        background = Color(0xFF000000),
        surface = Color(0xFF0D0D0D),
        surfaceVariant = Color(0xFF151515),
        outline = Color(0xFF262626),
        dotInactive = Color(0xFF1B1B1B),
    )

    BackgroundStyle.GRAPHITE -> BackgroundTones(
        background = Color(0xFF121212),
        surface = Color(0xFF1B1B1B),
        surfaceVariant = Color(0xFF222222),
        outline = Color(0xFF323232),
        dotInactive = Color(0xFF2A2A2A),
    )
}

private val SquareDot = RoundedCornerShape(1.dp)

object SlhPalette {
    var accent by mutableStateOf(AccentColor.RED.color)
        private set
    var onAccent by mutableStateOf(AccentColor.RED.onColor)
        private set
    var background by mutableStateOf(BackgroundStyle.AMOLED.tones().background)
        private set
    var surface by mutableStateOf(BackgroundStyle.AMOLED.tones().surface)
        private set
    var surfaceVariant by mutableStateOf(BackgroundStyle.AMOLED.tones().surfaceVariant)
        private set
    var outline by mutableStateOf(BackgroundStyle.AMOLED.tones().outline)
        private set
    var dotInactive by mutableStateOf(BackgroundStyle.AMOLED.tones().dotInactive)
        private set
    var dotShape by mutableStateOf<Shape>(CircleShape)
        private set
    var roundDots by mutableStateOf(true)
        private set

    fun apply(settings: AppSettings) {
        val tones = settings.background.tones()
        accent = settings.accent.color
        onAccent = settings.accent.onColor
        background = tones.background
        surface = tones.surface
        surfaceVariant = tones.surfaceVariant
        outline = tones.outline
        dotInactive = tones.dotInactive
        roundDots = settings.dotShape == DotShape.ROUND
        dotShape = if (roundDots) CircleShape else SquareDot
    }
}
