package com.slh.studentlabhub.navigation

import kotlinx.coroutines.flow.MutableStateFlow

object LaunchActions {
    const val TODAY = "com.slh.studentlabhub.action.TODAY"
    const val SUBJECTS = "com.slh.studentlabhub.action.SUBJECTS"
    const val REFERENCE = "com.slh.studentlabhub.action.REFERENCE"
    const val ADD_SUBJECT = "com.slh.studentlabhub.action.ADD_SUBJECT"

    private val known = setOf(TODAY, SUBJECTS, REFERENCE, ADD_SUBJECT)

    val pending = MutableStateFlow<String?>(null)

    val addSubjectRequested = MutableStateFlow(false)

    fun submit(action: String?) {
        if (action != null && action in known) {
            pending.value = action
        }
    }
}
