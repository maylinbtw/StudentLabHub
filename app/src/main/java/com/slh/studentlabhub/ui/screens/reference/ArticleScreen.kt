package com.slh.studentlabhub.ui.screens.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.R
import com.slh.studentlabhub.domain.model.ArticleBlock
import com.slh.studentlabhub.domain.model.RefArticle
import com.slh.studentlabhub.domain.model.RefArticleLink
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import kotlinx.coroutines.launch

private const val BLOCKS_OFFSET = 1

@Composable
fun ArticleRoute(
    onBack: () -> Unit,
    onOpenArticle: (RefArticleLink) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleViewModel = viewModel(factory = ArticleViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ArticleScreen(
        state = state,
        onBack = onBack,
        onOpenArticle = onOpenArticle,
        modifier = modifier,
    )
}

@Composable
fun ArticleScreen(
    state: ArticleUiState,
    onBack: () -> Unit,
    onOpenArticle: (RefArticleLink) -> Unit,
    modifier: Modifier = Modifier,
) {
    val article = state.article
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = article?.let {
                "${it.link.tech.label} · ${Strings.get(R.string.ref_chapter_n, it.chapter.number)} · ${it.chapter.title}"
            } ?: Strings.get(R.string.reference),
            title = article?.link?.title.orEmpty(),
            onBackClick = onBack,
        )
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = SlhAccent,
                    strokeWidth = 2.dp,
                )
            }

            article == null -> Text(
                text = Strings.get(R.string.article_not_found),
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
            )

            else -> ArticleContent(
                article = article,
                section = state.section,
                onOpenArticle = onOpenArticle,
            )
        }
    }
}

@Composable
private fun ArticleContent(
    article: RefArticle,
    section: String?,
    onOpenArticle: (RefArticleLink) -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var sectionHandled by rememberSaveable(article.link.id) { mutableStateOf(false) }
    val headings = article.headings

    LaunchedEffect(article.link.id, section) {
        if (section != null && !sectionHandled) {
            sectionHandled = true
            val index = article.blocks.indexOfFirst { it is ArticleBlock.Heading && it.text == section }
            if (index >= 0) listState.scrollToItem(index + BLOCKS_OFFSET)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
    ) {
        item(key = "contents") {
            if (headings.size >= 2) {
                Contents(
                    article = article,
                    onClick = { blockIndex ->
                        scope.launch { listState.animateScrollToItem(blockIndex + BLOCKS_OFFSET) }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        itemsIndexed(items = article.blocks) { _, block ->
            ArticleBlockView(block = block)
        }
        item(key = "pager") {
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlhOutline))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                PagerCard(
                    label = Strings.get(R.string.article_prev),
                    link = article.previous,
                    alignEnd = false,
                    onClick = onOpenArticle,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(10.dp))
                PagerCard(
                    label = Strings.get(R.string.article_next),
                    link = article.next,
                    alignEnd = true,
                    onClick = onOpenArticle,
                    modifier = Modifier.weight(1f),
                )
            }
            OfflineFooter()
        }
    }
}

@Composable
private fun Contents(
    article: RefArticle,
    onClick: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .padding(vertical = 10.dp),
    ) {
        SectionLabel(
            text = Strings.get(R.string.article_contents),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        )
        article.headings.forEachIndexed { number, (blockIndex, heading) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) { onClick(blockIndex) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text = "${number + 1}",
                    color = SlhAccent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.width(24.dp),
                )
                Text(
                    text = inlineText(heading.text),
                    color = SlhTextSecondary,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PagerCard(
    label: String,
    link: RefArticleLink?,
    alignEnd: Boolean,
    onClick: (RefArticleLink) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (link == null) {
        Spacer(modifier = modifier)
        return
    }
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button) { onClick(link) }
            .padding(12.dp),
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        Text(
            text = label,
            color = SlhAccent,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${link.number} ${link.title}",
            color = SlhContent,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        )
    }
}
