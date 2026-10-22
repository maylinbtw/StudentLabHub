package com.slh.studentlabhub.ui.screens.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.RefSource
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

@Composable
fun ReferenceCategoryRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReferenceCategoryViewModel = viewModel(factory = ReferenceCategoryViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val copyExample = rememberCopyExample()

    ReferenceCategoryScreen(
        state = state,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onEntryClick = copyExample,
        onSaveEntry = viewModel::saveEntry,
        onDeleteEntry = viewModel::deleteEntry,
        modifier = modifier,
    )
}

@Composable
fun ReferenceCategoryScreen(
    state: ReferenceCategoryUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onEntryClick: (RefEntry) -> Unit,
    onSaveEntry: (RefEntryDraft) -> Unit,
    onDeleteEntry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = state.category
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var editingEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    val editingEntry = editingEntryId?.let { id -> state.entries.firstOrNull { it.id == id } }
    val openEditor: (RefEntry?) -> Unit = { entry ->
        editingEntryId = entry?.id
        showEditor = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = category?.let { "Команды ${it.tech.label} · синтаксис и примеры" } ?: "Справочник",
            title = category?.title ?: "",
            onBackClick = onBack,
            actionIcon = if (category != null) SlhIcon.Plus else null,
            onActionClick = { openEditor(null) },
        )
        ReferenceSearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            placeholder = "Поиск…",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = SlhAccent,
                        strokeWidth = 2.dp,
                    )
                }
            }

            category == null -> {
                Text(
                    text = "В этом разделе больше нет статей",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                ) {
                    item(key = "label") {
                        SectionLabel(text = "${category.title.uppercase(Locale.getDefault())} · ${state.entries.size}")
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (state.entries.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = "Ничего не найдено",
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                            )
                        }
                    }
                    items(items = state.entries, key = { it.id }) { entry ->
                        EntryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry) },
                            onEdit = { openEditor(entry) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item(key = "hint") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Нажми на карточку, чтобы скопировать пример",
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OfflineFooter()
                    }
                }
            }
        }
    }

    if (showEditor && category != null) {
        val closeEditor = {
            showEditor = false
            editingEntryId = null
        }
        ReferenceEditorDialog(
            initial = editingEntry,
            defaultTech = category.tech,
            defaultCategoryId = state.categoryId,
            categories = state.allCategories,
            onDismiss = closeEditor,
            onSave = { draft ->
                onSaveEntry(draft)
                closeEditor()
            },
            onDelete = editingEntry?.let { entry ->
                {
                    onDeleteEntry(entry.id)
                    closeEditor()
                }
            },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 720)
@Composable
private fun ReferenceCategoryScreenPreview() {
    val category = RefCategory("1", RefTech.LINUX, "Файлы и директории", listOf("ls", "cd", "tree"))
    StudentLabHubTheme {
        ReferenceCategoryScreen(
            state = ReferenceCategoryUiState(
                isLoading = false,
                categoryId = category.id,
                category = category,
                allCategories = listOf(category),
                entries = listOf(
                    RefEntry("a", RefTech.LINUX, "1", "Файлы и директории", "ls", "Содержимое директории", "ls -la /home/user"),
                    RefEntry("b", RefTech.LINUX, "1", "Файлы и директории", "cd", "Смена директории", "cd /var/log"),
                    RefEntry(
                        id = "c",
                        tech = RefTech.LINUX,
                        categoryId = "1",
                        categoryTitle = "Файлы и директории",
                        title = "tree",
                        description = "Дерево каталогов",
                        example = "tree -L 2",
                        source = RefSource.CUSTOM,
                    ),
                ),
            ),
            onBack = {},
            onQueryChange = {},
            onEntryClick = {},
            onSaveEntry = {},
            onDeleteEntry = {},
        )
    }
}
