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
import com.slh.studentlabhub.domain.model.ArticleHit
import com.slh.studentlabhub.domain.model.RefArticleLink
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefChapter
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import androidx.compose.ui.text.style.TextOverflow
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
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

@Composable
fun ReferenceRoute(
    onArticleClick: (RefArticleLink, String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReferenceViewModel = viewModel(factory = ReferenceViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val copyExample = rememberCopyExample()

    ReferenceScreen(
        state = state,
        onTechSelected = viewModel::onTechSelected,
        onQueryChange = viewModel::onQueryChange,
        onArticleClick = onArticleClick,
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
    onEntryClick: (RefEntry) -> Unit,
    onDownloadTldr: () -> Unit,
    onSaveEntry: (RefEntryDraft) -> Unit,
    onDeleteEntry: (String) -> Unit,
    modifier: Modifier = Modifier,
    onArticleClick: (RefArticleLink, String?) -> Unit = { _, _ -> },
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
            overline = Strings.get(R.string.reference_overline),
            title = Strings.get(R.string.reference_title),
            actionIcon = SlhIcon.Plus,
            onActionClick = { openEditor(null) },
        )
        ReferenceSearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            placeholder = Strings.get(R.string.reference_search_hint),
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
                    onArticleClick = onArticleClick,
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
                ArticleChapters(
                    chapters = state.chapters,
                    notes = state.entriesByCategory,
                    showTldrTip = state.selectedTech == RefTech.LINUX,
                    onArticleClick = { link -> onArticleClick(link, null) },
                    onEntryClick = onEntryClick,
                    onEditEntry = openEditor,
                )
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
private fun ArticleChapters(
    chapters: List<RefChapter>,
    notes: List<Pair<RefCategory, List<RefEntry>>>,
    showTldrTip: Boolean,
    onArticleClick: (RefArticleLink) -> Unit,
    onEntryClick: (RefEntry) -> Unit,
    onEditEntry: (RefEntry) -> Unit,
) {
    val filledNotes = notes.filter { it.second.isNotEmpty() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    ) {
        chapters.forEach { chapter ->
            item(key = "chapter_${chapter.id}") {
                ChapterCard(chapter = chapter, onArticleClick = onArticleClick)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        filledNotes.forEach { (category, entries) ->
            item(key = "notes_${category.id}") {
                Spacer(modifier = Modifier.height(4.dp))
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
        }
        item(key = "footer") {
            val tips = listOfNotNull(
                Strings.get(R.string.tap_to_copy).takeIf { filledNotes.isNotEmpty() },
                Strings.get(R.string.reference_tip).takeIf { showTldrTip },
            )
            tips.forEach { tip ->
                Text(
                    text = tip,
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                )
            }
            OfflineFooter()
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: RefChapter,
    onArticleClick: (RefArticleLink) -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape),
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 6.dp)) {
            Text(
                text = Strings.get(R.string.ref_chapter_n, chapter.number).uppercase(Locale.getDefault()),
                color = SlhAccent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chapter.title,
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        chapter.articles.forEach { link ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) { onArticleClick(link) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = link.number,
                    color = SlhTextMuted,
                    fontFamily = SlhMono,
                    fontSize = 12.sp,
                    modifier = Modifier.width(40.dp),
                )
                Text(
                    text = link.title,
                    color = SlhTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "›",
                    color = SlhAccent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun ArticleHitCard(
    hit: ArticleHit,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(6.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(
            text = "${hit.article.number} ${hit.article.title}",
            color = SlhContent,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        if (hit.snippet.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hit.snippet,
                color = SlhTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = listOfNotNull(hit.article.tech.label, hit.chapterTitle, hit.section).joinToString(" › "),
            color = SlhTextDisabled,
            fontFamily = SlhMono,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun SearchResults(
    state: ReferenceUiState,
    onArticleClick: (RefArticleLink, String?) -> Unit,
    onEntryClick: (RefEntry) -> Unit,
    onEditEntry: (RefEntry) -> Unit,
    onDownloadTldr: () -> Unit,
) {
    val results = state.searchResults
    val hits = state.articleHits
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
    ) {
        if (hits.isNotEmpty()) {
            item(key = "articles_count") {
                SectionLabel(text = Strings.get(R.string.ref_articles_found, hits.size))
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(items = hits, key = { "hit_${it.article.tech}_${it.article.id}" }) { hit ->
                ArticleHitCard(
                    hit = hit,
                    onClick = { onArticleClick(hit.article, hit.section) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item(key = "articles_space") {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        if (results.isNotEmpty() || hits.isEmpty()) {
            item(key = "count") {
                SectionLabel(text = Strings.get(R.string.found_count, results.size))
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        if (results.isEmpty() && hits.isEmpty()) {
            item(key = "empty") {
                Text(
                    text = Strings.get(R.string.reference_not_found),
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
                        text = Strings.get(R.string.tldr_loading, command),
                        color = SlhTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            is TldrState.Saved -> {
                Text(
                    text = Strings.get(R.string.tldr_saved, tldrState.title),
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
                        text = Strings.get(R.string.tldr_find, command),
                        color = SlhAccent,
                        fontFamily = SlhMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
                val message = when (tldrState) {
                    is TldrState.NotFound -> Strings.get(R.string.tldr_not_found, tldrState.command)
                    is TldrState.Failed -> tldrState.message
                    else -> Strings.get(R.string.tldr_need_internet)
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

private val previewChapters = listOf(
    RefChapter(
        id = "basics",
        tech = RefTech.CSHARP,
        number = 1,
        title = "Основы C#",
        articles = listOf(
            RefArticleLink("intro", RefTech.CSHARP, "1.1", "Язык C# и платформа .NET"),
            RefArticleLink("first-program", RefTech.CSHARP, "1.2", "Первая программа"),
        ),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 720)
@Composable
private fun ReferenceScreenPreview() {
    StudentLabHubTheme {
        ReferenceScreen(
            state = ReferenceUiState(
                isLoading = false,
                selectedTech = RefTech.CSHARP,
                chapters = previewChapters,
            ),
            onTechSelected = {},
            onQueryChange = {},
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
            onEntryClick = {},
            onDownloadTldr = {},
            onSaveEntry = {},
            onDeleteEntry = {},
        )
    }
}
