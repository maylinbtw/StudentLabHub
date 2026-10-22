package com.slh.studentlabhub.navigation

import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.ui.components.SlhIcon

object Routes {
    const val LOGIN = "login"
    const val DEADLINES = "deadlines"
    const val LABS = "labs"
    const val REFERENCE = "reference"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    const val ARG_COURSE_ID = "courseId"
    const val COURSE_TASKS = "deadlines/course/{$ARG_COURSE_ID}"

    const val ARG_SUBJECT_ID = "subjectId"
    const val LAB_LIST = "labs/{$ARG_SUBJECT_ID}"

    const val ARG_LAB_ID = "labId"
    const val LAB_DETAILS = "labs/lab/{$ARG_LAB_ID}"

    const val STATISTICS = "labs/statistics/summary"

    const val ARG_REF_CATEGORY_ID = "refCategoryId"
    const val REFERENCE_CATEGORY = "reference/category/{$ARG_REF_CATEGORY_ID}"

    fun courseTasks(courseId: String): String = "deadlines/course/$courseId"

    fun labList(subjectId: Long): String = "labs/$subjectId"

    fun labDetails(labId: Long): String = "labs/lab/$labId"

    fun referenceCategory(categoryId: String): String = "reference/category/$categoryId"
}

val StartScreen.route: String
    get() = when (this) {
        StartScreen.DEADLINES -> Routes.DEADLINES
        StartScreen.LABS -> Routes.LABS
        StartScreen.REFERENCE -> Routes.REFERENCE
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
