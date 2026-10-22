package com.slh.studentlabhub.ui.screens.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.search
import com.slh.studentlabhub.domain.repository.ReferenceRepository
import com.slh.studentlabhub.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReferenceCategoryUiState(
    val isLoading: Boolean = true,
    val categoryId: String = "",
    val category: RefCategory? = null,
    val allCategories: List<RefCategory> = emptyList(),
    val query: String = "",
    val entries: List<RefEntry> = emptyList(),
)

class ReferenceCategoryViewModel(
    private val categoryId: String,
    private val repository: ReferenceRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<ReferenceCategoryUiState> = combine(
        repository.observeReference(),
        query,
    ) { data, text ->
        val category = data.categories.firstOrNull { it.id == categoryId }
        val entries = data.entries.filter { it.categoryId == categoryId }
        ReferenceCategoryUiState(
            isLoading = false,
            categoryId = categoryId,
            category = category,
            allCategories = data.categories,
            query = text,
            entries = if (text.isBlank()) entries else entries.search(text),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReferenceCategoryUiState(categoryId = categoryId),
    )

    fun onQueryChange(value: String) {
        query.value = value
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
                val savedStateHandle = createSavedStateHandle()
                val categoryId = checkNotNull(savedStateHandle.get<String>(Routes.ARG_REF_CATEGORY_ID)) {
                    "categoryId is required"
                }
                ReferenceCategoryViewModel(
                    categoryId = categoryId,
                    repository = app.container.referenceRepository,
                )
            }
        }
    }
}
