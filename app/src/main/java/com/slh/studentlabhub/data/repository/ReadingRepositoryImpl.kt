package com.slh.studentlabhub.data.repository

import android.content.Context
import com.slh.studentlabhub.domain.model.ReadingProgress
import com.slh.studentlabhub.domain.repository.ReadingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReadingRepositoryImpl(context: Context) : ReadingRepository {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val state = MutableStateFlow(load())

    override val progress: StateFlow<ReadingProgress> = state.asStateFlow()

    override fun setRead(key: String, read: Boolean) = update { current ->
        current.copy(read = if (read) current.read + key else current.read - key)
    }

    override fun toggleBookmark(key: String) = update { current ->
        val bookmarks = if (key in current.bookmarks) current.bookmarks - key else current.bookmarks + key
        current.copy(bookmarks = bookmarks)
    }

    override fun setLastArticle(key: String) = update { it.copy(lastArticle = key) }

    override fun setFontStep(step: Int) = update {
        it.copy(fontStep = step.coerceIn(ReadingProgress.FONT_SCALES.indices))
    }

    override fun restore(progress: ReadingProgress) = update { progress }

    @Synchronized
    private fun update(transform: (ReadingProgress) -> ReadingProgress) {
        val updated = transform(state.value)
        if (updated == state.value) return
        prefs.edit()
            .putStringSet(KEY_READ, updated.read)
            .putStringSet(KEY_BOOKMARKS, updated.bookmarks)
            .putString(KEY_LAST_ARTICLE, updated.lastArticle)
            .putInt(KEY_FONT_STEP, updated.fontStep)
            .apply()
        state.value = updated
    }

    private fun load(): ReadingProgress = ReadingProgress(
        read = prefs.getStringSet(KEY_READ, emptySet())?.toSet().orEmpty(),
        bookmarks = prefs.getStringSet(KEY_BOOKMARKS, emptySet())?.toSet().orEmpty(),
        lastArticle = prefs.getString(KEY_LAST_ARTICLE, null),
        fontStep = prefs.getInt(KEY_FONT_STEP, ReadingProgress.DEFAULT_FONT_STEP),
    )

    private companion object {
        const val PREFS_NAME = "slh_reading"
        const val KEY_READ = "read"
        const val KEY_BOOKMARKS = "bookmarks"
        const val KEY_LAST_ARTICLE = "last_article"
        const val KEY_FONT_STEP = "font_step"
    }
}
