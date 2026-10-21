package com.slh.studentlabhub.ui.components

import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.WorkType

val WorkType.shortPrefix: String
    get() = when (this) {
        WorkType.LAB -> "ЛР"
        WorkType.PRACTICE -> "ПР"
    }

val WorkType.sectionTitle: String
    get() = when (this) {
        WorkType.LAB -> "ЛАБОРАТОРНЫЕ"
        WorkType.PRACTICE -> "ПРАКТИЧЕСКИЕ"
    }

val WorkType.completedLabel: String
    get() = when (this) {
        WorkType.LAB -> "Защищено"
        WorkType.PRACTICE -> "Сдано"
    }

val WorkType.addButtonLabel: String
    get() = when (this) {
        WorkType.LAB -> "лабораторная"
        WorkType.PRACTICE -> "практическая"
    }

val Lab.displayName: String
    get() = "${type.shortPrefix} №$number · $title"