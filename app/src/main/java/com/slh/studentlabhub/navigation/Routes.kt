package com.slh.studentlabhub.navigation

import com.slh.studentlabhub.ui.components.SlhIcon

object Routes {
    const val LOGIN = "login"
    const val DEADLINES = "deadlines"
    const val LABS = "labs"
    const val REFERENCE = "reference"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    const val ARG_SUBJECT_ID = "subjectId"
    const val LAB_LIST = "labs/{$ARG_SUBJECT_ID}"

    const val ARG_LAB_ID = "labId"
    const val LAB_DETAILS = "labs/lab/{$ARG_LAB_ID}"

    const val STATISTICS = "labs/statistics/summary"

    fun labList(subjectId: Long): String = "labs/$subjectId"

    fun labDetails(labId: Long): String = "labs/lab/$labId"
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
        fun fromRoute(route: String?): BottomTab? {
            if (route == null) return null
            return entries.firstOrNull { tab ->
                route == tab.route || route.startsWith(tab.route + "/")
            }
        }
    }
}