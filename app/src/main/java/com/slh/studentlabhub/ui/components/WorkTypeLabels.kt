package com.slh.studentlabhub.ui.components

import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

val WorkType.shortPrefix: String
    get() = when (this) {
        WorkType.LAB -> Strings.get(R.string.prefix_lab)
        WorkType.PRACTICE -> Strings.get(R.string.prefix_practice)
    }

val WorkType.sectionTitle: String
    get() = when (this) {
        WorkType.LAB -> Strings.get(R.string.section_labs)
        WorkType.PRACTICE -> Strings.get(R.string.section_practice)
    }

val WorkType.completedLabel: String
    get() = when (this) {
        WorkType.LAB -> Strings.get(R.string.completed_lab)
        WorkType.PRACTICE -> Strings.get(R.string.completed_practice)
    }

val WorkType.addButtonLabel: String
    get() = when (this) {
        WorkType.LAB -> Strings.get(R.string.add_lab)
        WorkType.PRACTICE -> Strings.get(R.string.add_practice)
    }

val Lab.displayName: String
    get() = Strings.get(R.string.work_display_name, type.shortPrefix, number, title)