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

enum class AppLanguage {
    SYSTEM,
    RU,
    EN,
}

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM,
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
    TODAY,
    SUBJECTS,
    REFERENCE,
}

enum class ReminderTiming {
    DAY,
    HOURS,
    BOTH,
}

data class AppSettings(
    val accent: AccentColor = AccentColor.RED,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val background: BackgroundStyle = BackgroundStyle.AMOLED,
    val dotShape: DotShape = DotShape.ROUND,
    val startScreen: StartScreen = StartScreen.TODAY,
    val remindersEnabled: Boolean = true,
    val reminderTiming: ReminderTiming = ReminderTiming.BOTH,
    val redoAlerts: Boolean = true,
    val backgroundSync: Boolean = true,
    val largeText: Boolean = false,
    val language: AppLanguage = AppLanguage.SYSTEM,
) {
    val needsBackgroundWork: Boolean
        get() = remindersEnabled || redoAlerts || backgroundSync
}
