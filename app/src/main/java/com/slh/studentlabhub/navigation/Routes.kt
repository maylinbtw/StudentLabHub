package com.slh.studentlabhub.navigation

import com.slh.studentlabhub.domain.model.StartScreen
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import androidx.annotation.StringRes

object Routes {
    const val LOGIN = "login"
    const val TODAY = "today"
    const val SUBJECTS = "subjects"
    const val REFERENCE = "reference"
    const val SETTINGS = "settings"

    const val STATISTICS = "statistics"

    const val ARCHIVE = "archive"

    const val SCHEDULE = "today/schedule"

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
    @StringRes val labelRes: Int,
    val icon: SlhIcon,
) {
    Today(Routes.TODAY, R.string.tab_today, SlhIcon.Grid),
    Subjects(Routes.SUBJECTS, R.string.tab_subjects, SlhIcon.Diamond),
    Reference(Routes.REFERENCE, R.string.tab_reference, SlhIcon.Menu),
    Settings(Routes.SETTINGS, R.string.tab_settings, SlhIcon.Gear);

    val label: String
        get() = Strings.get(labelRes)

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
