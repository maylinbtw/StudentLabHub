package com.slh.studentlabhub.ui.screens.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.domain.model.TldrResult
import com.slh.studentlabhub.domain.model.search
import com.slh.studentlabhub.domain.model.tldrCommandOrNull
import com.slh.studentlabhub.domain.repository.ReferenceRepository
import com.slh.studentlabhub.domain.model.ArticleHit
import com.slh.studentlabhub.domain.model.RefChapter
import com.slh.studentlabhub.domain.repository.ArticleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TldrState {
    data object Idle : TldrState
    data object Loading : TldrState
    data class Saved(val title: String) : TldrState
    data class NotFound(val command: String) : TldrState
    data class Failed(val message: String) : TldrState
}

data class ReferenceUiState(
    val isLoading: Boolean = true,
    val selectedTech: RefTech = RefTech.LINUX,
    val query: String = "",
    val allCategories: List<RefCategory> = emptyList(),
    val categories: List<RefCategory> = emptyList(),
    val entriesByCategory: List<Pair<RefCategory, List<RefEntry>>> = emptyList(),
    val searchResults: List<RefEntry> = emptyList(),
    val tldrCommand: String? = null,
    val tldrState: TldrState = TldrState.Idle,
    val chapters: List<RefChapter> = emptyList(),
    val articleHits: List<ArticleHit> = emptyList(),
) {
    val isSearching: Boolean
        get() = query.isNotBlank()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReferenceViewModel(
    private val repository: ReferenceRepository,
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val selectedTech = MutableStateFlow(RefTech.LINUX)
    private val query = MutableStateFlow("")
    private val tldrState = MutableStateFlow<TldrState>(TldrState.Idle)

    private val chapters = selectedTech.mapLatest { tech -> articleRepository.chapters(tech) }

    private val articleHits = query.mapLatest { text -> articleRepository.search(text) }

    private val baseState = combine(
        repository.observeReference(),
        selectedTech,
        query,
        tldrState,
    ) { data, tech, text, tldr ->
        val categories = data.categories.filter { it.tech == tech }
        val entriesByCategory = categories.map { category ->
            category to data.entries.filter { it.categoryId == category.id }
        }
        ReferenceUiState(
            isLoading = false,
            selectedTech = tech,
            query = text,
            allCategories = data.categories,
            categories = categories,
            entriesByCategory = entriesByCategory,
            searchResults = data.entries.search(text),
            tldrCommand = tldrCommandOrNull(text),
            tldrState = tldr,
        )
    }

    val uiState: StateFlow<ReferenceUiState> = combine(
        baseState,
        chapters,
        articleHits,
    ) { base, techChapters, hits ->
        base.copy(
            chapters = techChapters.filter { it.tech == base.selectedTech },
            articleHits = if (base.isSearching) hits else emptyList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReferenceUiState(),
    )

    fun onTechSelected(tech: RefTech) {
        selectedTech.value = tech
    }

    fun onQueryChange(value: String) {
        query.value = value
        tldrState.value = TldrState.Idle
    }

    fun downloadFromTldr() {
        val command = tldrCommandOrNull(query.value) ?: return
        if (tldrState.value == TldrState.Loading) return
        tldrState.value = TldrState.Loading
        viewModelScope.launch {
            tldrState.value = when (val result = repository.downloadFromTldr(command)) {
                is TldrResult.Saved -> TldrState.Saved(result.title)
                TldrResult.NotFound -> TldrState.NotFound(command)
                is TldrResult.Failed -> TldrState.Failed(result.message)
            }
        }
    }

    fun saveEntry(draft: RefEntryDraft) {
        viewModelScope.launch {
            repository.saveCustomEntry(draft)
        }
    }

    fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                ReferenceViewModel(
                    repository = app.container.referenceRepository,
                    articleRepository = app.container.articleRepository,
                )
            }
        }
    }
}
