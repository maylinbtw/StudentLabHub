package com.slh.studentlabhub.navigation

import com.slh.studentlabhub.ui.components.SlhIcon

object Routes {
    const val LOGIN = "login"
    const val DEADLINES = "deadlines"
    const val LABS = "labs"
    const val REFERENCE = "reference"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
}

enum class BottomTab(
    val route: String,
    val label: String,
    val icon: SlhIcon,
) {
    Deadlines(Routes.DEADLINES, "Дедлайны", SlhIcon.Grid),
    Labs(Routes.LABS, "Лабораторные", SlhIcon.Diamond),
    Reference(Routes.REFERENCE, "Справка", SlhIcon.Menu),
    Calendar(Routes.CALENDAR, "Календарь", SlhIcon.Square),
    Settings(Routes.SETTINGS, "Настройки", SlhIcon.Gear);

    companion object {
        fun fromRoute(route: String?): BottomTab? = entries.firstOrNull { it.route == route }
    }
}