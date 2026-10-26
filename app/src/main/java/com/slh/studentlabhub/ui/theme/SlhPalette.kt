package com.slh.studentlabhub.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.domain.model.AccentColor
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.BackgroundStyle
import com.slh.studentlabhub.domain.model.DotShape
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

private val PureWhite = Color(0xFFFFFFFF)
private val PureBlack = Color(0xFF000000)

val AccentColor.color: Color
    get() = colorFor(SlhPalette.isDark)

fun AccentColor.colorFor(dark: Boolean): Color = if (dark) darkVariant() else lightVariant()

private fun AccentColor.darkVariant(): Color = when (this) {
    AccentColor.RED -> Color(0xFFFF3B30)
    AccentColor.ORANGE -> Color(0xFFFF8A1F)
    AccentColor.YELLOW -> Color(0xFFFFD60A)
    AccentColor.GREEN -> Color(0xFF32D74B)
    AccentColor.CYAN -> Color(0xFF40C8E0)
    AccentColor.BLUE -> Color(0xFF0A84FF)
    AccentColor.VIOLET -> Color(0xFFBF5AF2)
    AccentColor.PINK -> Color(0xFFFF375F)
}

private fun AccentColor.lightVariant(): Color = when (this) {
    AccentColor.RED -> Color(0xFFE5291E)
    AccentColor.ORANGE -> Color(0xFFE86A00)
    AccentColor.YELLOW -> Color(0xFFC79200)
    AccentColor.GREEN -> Color(0xFF1E9E36)
    AccentColor.CYAN -> Color(0xFF0090AD)
    AccentColor.BLUE -> Color(0xFF0A6FE0)
    AccentColor.VIOLET -> Color(0xFF9B3FD1)
    AccentColor.PINK -> Color(0xFFE5234C)
}

private fun contentOn(color: Color): Color = if (color.luminance() > 0.45f) PureBlack else PureWhite

val AccentColor.label: String
    get() = when (this) {
        AccentColor.RED -> Strings.get(R.string.accent_red)
        AccentColor.ORANGE -> Strings.get(R.string.accent_orange)
        AccentColor.YELLOW -> Strings.get(R.string.accent_yellow)
        AccentColor.GREEN -> Strings.get(R.string.accent_green)
        AccentColor.CYAN -> Strings.get(R.string.accent_cyan)
        AccentColor.BLUE -> Strings.get(R.string.accent_blue)
        AccentColor.VIOLET -> Strings.get(R.string.accent_violet)
        AccentColor.PINK -> Strings.get(R.string.accent_pink)
    }

private class Tones(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val dotInactive: Color,
    val content: Color,
    val onContent: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textDisabled: Color,
)

private fun darkTones(style: BackgroundStyle): Tones = when (style) {
    BackgroundStyle.AMOLED -> Tones(
        background = Color(0xFF000000),
        surface = Color(0xFF0D0D0D),
        surfaceVariant = Color(0xFF151515),
        outline = Color(0xFF262626),
        dotInactive = Color(0xFF1B1B1B),
        content = PureWhite,
        onContent = PureBlack,
        textSecondary = Color(0xFF939393),
        textMuted = Color(0xFF595959),
        textDisabled = Color(0xFF3A3A3A),
    )

    BackgroundStyle.GRAPHITE -> Tones(
        background = Color(0xFF121212),
        surface = Color(0xFF1B1B1B),
        surfaceVariant = Color(0xFF222222),
        outline = Color(0xFF323232),
        dotInactive = Color(0xFF2A2A2A),
        content = PureWhite,
        onContent = PureBlack,
        textSecondary = Color(0xFF9A9A9A),
        textMuted = Color(0xFF666666),
        textDisabled = Color(0xFF454545),
    )
}

private val LightTones = Tones(
    background = Color(0xFFF2F2F2),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9E9E9),
    outline = Color(0xFFD6D6D6),
    dotInactive = Color(0xFFDDDDDD),
    content = Color(0xFF111111),
    onContent = PureWhite,
    textSecondary = Color(0xFF5C5C5C),
    textMuted = Color(0xFF8C8C8C),
    textDisabled = Color(0xFFBDBDBD),
)

private val SquareDot = RoundedCornerShape(1.dp)

private const val LARGE_TEXT_SCALE = 1.15f

object SlhPalette {
    var isDark by mutableStateOf(true)
        private set
    var accent by mutableStateOf(AccentColor.RED.darkVariant())
        private set
    var onAccent by mutableStateOf(contentOn(AccentColor.RED.darkVariant()))
        private set
    var background by mutableStateOf(darkTones(BackgroundStyle.AMOLED).background)
        private set
    var surface by mutableStateOf(darkTones(BackgroundStyle.AMOLED).surface)
        private set
    var surfaceVariant by mutableStateOf(darkTones(BackgroundStyle.AMOLED).surfaceVariant)
        private set
    var outline by mutableStateOf(darkTones(BackgroundStyle.AMOLED).outline)
        private set
    var dotInactive by mutableStateOf(darkTones(BackgroundStyle.AMOLED).dotInactive)
        private set
    var content by mutableStateOf(PureWhite)
        private set
    var onContent by mutableStateOf(PureBlack)
        private set
    var textSecondary by mutableStateOf(darkTones(BackgroundStyle.AMOLED).textSecondary)
        private set
    var textMuted by mutableStateOf(darkTones(BackgroundStyle.AMOLED).textMuted)
        private set
    var textDisabled by mutableStateOf(darkTones(BackgroundStyle.AMOLED).textDisabled)
        private set
    var dotShape by mutableStateOf<Shape>(CircleShape)
        private set
    var roundDots by mutableStateOf(true)
        private set
    var fontScale by mutableStateOf(1f)
        private set

    fun apply(settings: AppSettings, dark: Boolean) {
        val tones = if (dark) darkTones(settings.background) else LightTones
        val accentColor = if (dark) settings.accent.darkVariant() else settings.accent.lightVariant()
        isDark = dark
        accent = accentColor
        onAccent = contentOn(accentColor)
        background = tones.background
        surface = tones.surface
        surfaceVariant = tones.surfaceVariant
        outline = tones.outline
        dotInactive = tones.dotInactive
        content = tones.content
        onContent = tones.onContent
        textSecondary = tones.textSecondary
        textMuted = tones.textMuted
        textDisabled = tones.textDisabled
        roundDots = settings.dotShape == DotShape.ROUND
        dotShape = if (roundDots) CircleShape else SquareDot
        fontScale = if (settings.largeText) LARGE_TEXT_SCALE else 1f
    }
}
