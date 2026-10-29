package com.slh.studentlabhub.ui.screens.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.ReadingProgress
import com.slh.studentlabhub.domain.model.RefArticle
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.domain.model.articleKey
import com.slh.studentlabhub.domain.repository.ArticleRepository
import com.slh.studentlabhub.domain.repository.ReadingRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ArticleUiState(
    val isLoading: Boolean = true,
    val article: RefArticle? = null,
    val section: String? = null,
    val isRead: Boolean = false,
    val isBookmarked: Boolean = false,
    val fontStep: Int = ReadingProgress.DEFAULT_FONT_STEP,
) {
    val fontScale: Float
        get() = ReadingProgress.FONT_SCALES[fontStep.coerceIn(ReadingProgress.FONT_SCALES.indices)]

    val canDecreaseFont: Boolean
        get() = fontStep > 0

    val canIncreaseFont: Boolean
        get() = fontStep < ReadingProgress.FONT_SCALES.lastIndex
}

class ArticleViewModel(
    tech: RefTech,
    private val articleId: String,
    section: String?,
    private val repository: ArticleRepository,
    private val readingRepository: ReadingRepository,
) : ViewModel() {

    private val key = articleKey(tech, articleId)
    private val loaded = MutableStateFlow(ArticleUiState(section = section))

    val uiState: StateFlow<ArticleUiState> = combine(loaded, readingRepository.progress) { base, progress ->
        base.copy(
            isRead = key in progress.read,
            isBookmarked = key in progress.bookmarks,
            fontStep = progress.fontStep,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = loaded.value,
    )

    init {
        viewModelScope.launch {
            val article = repository.article(tech, articleId)
            if (article != null) readingRepository.setLastArticle(key)
            loaded.value = loaded.value.copy(isLoading = false, article = article)
        }
    }

    fun onReachedEnd() {
        if (key !in readingRepository.progress.value.read) readingRepository.setRead(key, true)
    }

    fun toggleRead() {
        readingRepository.setRead(key, key !in readingRepository.progress.value.read)
    }

    fun toggleBookmark() {
        readingRepository.toggleBookmark(key)
    }

    fun changeFont(delta: Int) {
        readingRepository.setFontStep(readingRepository.progress.value.fontStep + delta)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                val savedStateHandle = createSavedStateHandle()
                val techName = checkNotNull(savedStateHandle.get<String>(Routes.ARG_REF_TECH)) { "tech is required" }
                val articleId = checkNotNull(savedStateHandle.get<String>(Routes.ARG_REF_ARTICLE_ID)) { "articleId is required" }
                ArticleViewModel(
                    tech = RefTech.entries.firstOrNull { it.name == techName } ?: RefTech.CSHARP,
                    articleId = articleId,
                    section = savedStateHandle.get<String>(Routes.ARG_REF_SECTION)?.takeIf { it.isNotBlank() },
                    repository = app.container.articleRepository,
                    readingRepository = app.container.readingRepository,
                )
            }
        }
    }
}
