package com.slh.studentlabhub.data.local

import android.content.Context

class ReminderStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var notifiedKeys: Set<String>
        get() = prefs.getStringSet(KEY_NOTIFIED, emptySet())?.toSet().orEmpty()
        set(value) {
            prefs.edit().putStringSet(KEY_NOTIFIED, value.toSet()).apply()
        }

    var initialized: Boolean
        get() = prefs.getBoolean(KEY_INITIALIZED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_INITIALIZED, value).apply()
        }

    private companion object {
        const val PREFS_NAME = "slh_reminders"
        const val KEY_NOTIFIED = "notified_keys"
        const val KEY_INITIALIZED = "initialized"
    }
}
