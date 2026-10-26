package com.slh.studentlabhub.navigation

import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.ui.components.SlhIcon

object Routes {
    const val LOGIN = "login"
    const val TODAY = "today"
    const val SUBJECTS = "subjects"
    const val REFERENCE = "reference"
    const val SETTINGS = "settings"

    const val STATISTICS = "statistics"

    const val ARCHIVE = "archive"

    const val ARG_SUBJECT_ID = "subjectId"
    const val SUBJECT = "subjects/{$ARG_SUBJECT_ID}"

    const val ARG_LAB_ID = "labId"
    const val LAB_DETAILS = "subjects/lab/{$ARG_LAB_ID}"


    const val ARG_REF_CATEGORY_ID = "refCategoryId"
    const val REFERENCE_CATEGORY = "reference/category/{$ARG_REF_CATEGORY_ID}"

    fun subject(subjectId: Long): String = "subjects/$subjectId"

    fun labDetails(labId: Long): String = "subjects/lab/$labId"

    fun referenceCategory(categoryId: String): String = "reference/category/$categoryId"
}

val StartScreen.route: String
    get() = when (this) {
        StartScreen.TODAY -> Routes.TODAY
        StartScreen.SUBJECTS -> Routes.SUBJECTS
        StartScreen.REFERENCE -> Routes.REFERENCE
    }

enum class BottomTab(
    val route: String,
    val label: String,
    val icon: SlhIcon,
) {
    Today(Routes.TODAY, "Сегодня", SlhIcon.Grid),
    Subjects(Routes.SUBJECTS, "Предметы", SlhIcon.Diamond),
    Reference(Routes.REFERENCE, "Справка", SlhIcon.Menu),
    Settings(Routes.SETTINGS, "Настройки", SlhIcon.Gear);

    companion object {
        fun fromRoute(route: String?): BottomTab? =
            entries.firstOrNull { it.route == route }

        fun fromPrefix(route: String): BottomTab? = when {
            route.startsWith("today") -> Today
            route.startsWith("subjects") || route == Routes.ARCHIVE || route == Routes.STATISTICS -> Subjects
            route.startsWith("reference") -> Reference
            route.startsWith("settings") -> Settings
            else -> null
        }
    }
}
