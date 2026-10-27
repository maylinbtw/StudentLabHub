package com.slh.studentlabhub.data.local

import android.content.Context

class SessionStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) {
            prefs.edit().putString(KEY_EMAIL, value).apply()
        }

    var lastSyncAt: Long?
        get() = prefs.getLong(KEY_LAST_SYNC, 0L).takeIf { it > 0L }
        set(value) {
            prefs.edit().putLong(KEY_LAST_SYNC, value ?: 0L).apply()
        }

    var scheduleSyncAt: Long?
        get() = prefs.getLong(KEY_SCHEDULE_SYNC, 0L).takeIf { it > 0L }
        set(value) {
            prefs.edit().putLong(KEY_SCHEDULE_SYNC, value ?: 0L).apply()
        }

    var redoThreshold: Int
        get() = prefs.getInt(KEY_REDO_THRESHOLD, DEFAULT_REDO_THRESHOLD)
        set(value) {
            prefs.edit().putInt(KEY_REDO_THRESHOLD, value).apply()
        }

    var referenceVersion: Int
        get() = prefs.getInt(KEY_REFERENCE_VERSION, 0)
        set(value) {
            prefs.edit().putInt(KEY_REFERENCE_VERSION, value).apply()
        }

    var referenceLanguage: String
        get() = prefs.getString(KEY_REFERENCE_LANGUAGE, "").orEmpty()
        set(value) {
            prefs.edit().putString(KEY_REFERENCE_LANGUAGE, value).apply()
        }

    var archivedCourseIds: Set<String>
        get() = prefs.getStringSet(KEY_ARCHIVED_COURSES, emptySet())?.toSet().orEmpty()
        set(value) {
            prefs.edit().putStringSet(KEY_ARCHIVED_COURSES, value.toSet()).apply()
        }

    fun clear() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_LAST_SYNC)
            .apply()
    }

    companion object {
        const val DEFAULT_REDO_THRESHOLD = 1

        private const val PREFS_NAME = "slh_session"
        private const val KEY_EMAIL = "email"
        private const val KEY_LAST_SYNC = "last_sync_at"
        private const val KEY_REDO_THRESHOLD = "redo_threshold"
        private const val KEY_REFERENCE_VERSION = "reference_version"
        private const val KEY_SCHEDULE_SYNC = "schedule_sync_at"
        private const val KEY_REFERENCE_LANGUAGE = "reference_language"
        private const val KEY_ARCHIVED_COURSES = "archived_courses"
    }
}
