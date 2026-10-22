package com.slh.studentlabhub.domain.model

enum class AccentColor {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    CYAN,
    BLUE,
    VIOLET,
    PINK,
}

enum class BackgroundStyle {
    AMOLED,
    GRAPHITE,
}

enum class DotShape {
    ROUND,
    SQUARE,
}

enum class StartScreen {
    DEADLINES,
    LABS,
    REFERENCE,
}

data class AppSettings(
    val accent: AccentColor = AccentColor.RED,
    val background: BackgroundStyle = BackgroundStyle.AMOLED,
    val dotShape: DotShape = DotShape.ROUND,
    val startScreen: StartScreen = StartScreen.DEADLINES,
)
