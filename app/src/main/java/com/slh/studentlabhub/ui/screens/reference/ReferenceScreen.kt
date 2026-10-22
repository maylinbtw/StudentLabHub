package com.slh.studentlabhub.ui.screens.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

@Composable
fun ReferenceRoute(
    onCategoryClick: (RefCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReferenceViewModel = viewModel(factory = ReferenceViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val copyExample = rememberCopyExample()

    ReferenceScreen(
        state = state,
        onTechSelected = viewModel::onTechSelected,
        onQueryChange = viewModel::onQueryChange,
        onCategoryClick = onCategoryClick,
        onEntryClick = copyExample,
        onDownloadTldr = viewModel::downloadFromTldr,
        onSaveEntry = viewModel::saveEntry,
        onDeleteEntry = viewModel::deleteEntry,
        modifier = modifier,
    )
}

@Composable
fun ReferenceScreen(
    state: ReferenceUiState,
    onTechSelected: (RefTech) -> Unit,
    onQueryChange: (String) -> Unit,
    onCategoryClick: (RefCategory) -> Unit,
    onEntryClick: (RefEntry) -> Unit,
    onDownloadTldr: () -> Unit,
    onSaveEntry: (RefEntryDraft) -> Unit,
    onDeleteEntry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var editingEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    val editingEntry = editingEntryId?.let { id ->
        state.searchResults.firstOrNull { it.id == id }
            ?: state.entriesByCategory.flatMap { it.second }.firstOrNull { it.id == id }
    }
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
            overline = "Работает без интернета",
            title = "СПРАВОЧНИК",
            actionIcon = SlhIcon.Plus,
            onActionClick = { openEditor(null) },
        )
        ReferenceSearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            placeholder = "Поиск команд, синтаксиса…",
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

            state.isSearching -> {
                SearchResults(
                    state = state,
                    onEntryClick = onEntryClick,
                    onEditEntry = openEditor,
                    onDownloadTldr = onDownloadTldr,
                )
            }

            else -> {
                TechTabs(
                    selected = state.selectedTech,
                    onSelect = onTechSelected,
                )
                if (state.selectedTech == RefTech.LINUX) {
                    LinuxCategories(
                        categories = state.categories,
                        onCategoryClick = onCategoryClick,
                    )
                } else {
                    LanguageCheatSheet(
                        groups = state.entriesByCategory,
                        onEntryClick = onEntryClick,
                        onEditEntry = openEditor,
                    )
                }
            }
        }
    }

    if (showEditor) {
        val closeEditor = {
            showEditor = false
            editingEntryId = null
        }
        ReferenceEditorDialog(
            initial = editingEntry,
            defaultTech = state.selectedTech,
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

@Composable
private fun LinuxCategories(
    categories: List<RefCategory>,
    onCategoryClick: (RefCategory) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    ) {
        item(key = "label") {
            SectionLabel(text = "КАТЕГОРИИ КОМАНД · ${categories.sumOf { it.entryTitles.size }}")
            Spacer(modifier = Modifier.height(10.dp))
        }
        items(items = categories, key = { it.id }) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category) },
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item(key = "footer") {
            Text(
                text = "Нет нужной команды? Найди её через поиск — её можно скачать из tldr-pages. Свою статью добавь кнопкой «+».",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp),
            )
            OfflineFooter()
        }
    }
}

@Composable
private fun LanguageCheatSheet(
    groups: List<Pair<RefCategory, List<RefEntry>>>,
    onEntryClick: (RefEntry) -> Unit,
    onEditEntry: (RefEntry) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    ) {
        groups.forEach { (category, entries) ->
            item(key = "group_${category.id}") {
                SectionLabel(text = category.title.uppercase(Locale.getDefault()))
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(items = entries, key = { it.id }) { entry ->
                EntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    onEdit = { onEditEntry(entry) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item(key = "space_${category.id}") {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        item(key = "hint") {
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

@Composable
private fun SearchResults(
    state: ReferenceUiState,
    onEntryClick: (RefEntry) -> Unit,
    onEditEntry: (RefEntry) -> Unit,
    onDownloadTldr: () -> Unit,
) {
    val results = state.searchResults
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
    ) {
        item(key = "count") {
            SectionLabel(text = "НАЙДЕНО: ${results.size}")
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (results.isEmpty()) {
            item(key = "empty") {
                Text(
                    text = "В справочнике такого нет.",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                )
            }
        }
        items(items = results, key = { it.id }) { entry ->
            EntryCard(
                entry = entry,
                onClick = { onEntryClick(entry) },
                showPath = true,
                onEdit = { onEditEntry(entry) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        val command = state.tldrCommand
        if (command != null) {
            item(key = "tldr") {
                Spacer(modifier = Modifier.height(8.dp))
                TldrBlock(
                    command = command,
                    tldrState = state.tldrState,
                    onDownload = onDownloadTldr,
                )
            }
        }
    }
}

@Composable
private fun TldrBlock(
    command: String,
    tldrState: TldrState,
    onDownload: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(modifier = Modifier.fillMaxWidth()) {
        when (tldrState) {
            TldrState.Loading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = SlhAccent,
                        strokeWidth = 1.5.dp,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Загружаем «$command» из tldr-pages…",
                        color = SlhTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            is TldrState.Saved -> {
                Text(
                    text = "«${tldrState.title}» сохранено в «Загружено из tldr-pages» и доступно офлайн",
                    color = SlhTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            else -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(shape)
                        .border(1.dp, SlhOutline, shape)
                        .clickable(role = Role.Button, onClick = onDownload),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "↓ найти «$command» в tldr-pages",
                        color = SlhAccent,
                        fontFamily = SlhMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
                val message = when (tldrState) {
                    is TldrState.NotFound -> "В tldr-pages нет команды «${tldrState.command}»"
                    is TldrState.Failed -> tldrState.message
                    else -> "Нужен интернет один раз — потом статья доступна офлайн"
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message,
                    color = if (tldrState is TldrState.Failed || tldrState is TldrState.NotFound) SlhDanger else SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private val previewCategories = listOf(
    RefCategory("1", RefTech.LINUX, "Файлы и директории", listOf("ls", "cd", "mkdir", "rm", "cp", "mv")),
    RefCategory("2", RefTech.LINUX, "Процессы", listOf("ps", "kill", "top", "htop")),
    RefCategory("3", RefTech.LINUX, "Права доступа", listOf("chmod", "chown", "umask")),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 720)
@Composable
private fun ReferenceScreenPreview() {
    StudentLabHubTheme {
        ReferenceScreen(
            state = ReferenceUiState(
                isLoading = false,
                categories = previewCategories,
            ),
            onTechSelected = {},
            onQueryChange = {},
            onCategoryClick = {},
            onEntryClick = {},
            onDownloadTldr = {},
            onSaveEntry = {},
            onDeleteEntry = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 560)
@Composable
private fun ReferenceSearchPreview() {
    StudentLabHubTheme {
        ReferenceScreen(
            state = ReferenceUiState(
                isLoading = false,
                query = "rsync",
                tldrCommand = "rsync",
            ),
            onTechSelected = {},
            onQueryChange = {},
            onCategoryClick = {},
            onEntryClick = {},
            onDownloadTldr = {},
            onSaveEntry = {},
            onDeleteEntry = {},
        )
    }
}
