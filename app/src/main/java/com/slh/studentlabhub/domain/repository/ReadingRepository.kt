package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.ReadingProgress
import kotlinx.coroutines.flow.StateFlow

interface ReadingRepository {
    val progress: StateFlow<ReadingProgress>

    fun setRead(key: String, read: Boolean)

    fun toggleBookmark(key: String)

    fun setLastArticle(key: String)

    fun setFontStep(step: Int)

    fun restore(progress: ReadingProgress)
}
