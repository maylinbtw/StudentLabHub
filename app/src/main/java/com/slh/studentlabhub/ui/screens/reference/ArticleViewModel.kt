package com.slh.studentlabhub.ui.screens.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.RefArticle
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.domain.repository.ArticleRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArticleUiState(
    val isLoading: Boolean = true,
    val article: RefArticle? = null,
    val section: String? = null,
)

class ArticleViewModel(
    private val tech: RefTech,
    private val articleId: String,
    section: String?,
    private val repository: ArticleRepository,
) : ViewModel() {

    private val state = MutableStateFlow(ArticleUiState(section = section))
    val uiState: StateFlow<ArticleUiState> = state.asStateFlow()

    init {
        viewModelScope.launch {
            val article = repository.article(tech, articleId)
            state.value = state.value.copy(isLoading = false, article = article)
        }
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
                )
            }
        }
    }
}
