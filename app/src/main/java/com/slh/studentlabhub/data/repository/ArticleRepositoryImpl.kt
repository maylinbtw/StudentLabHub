package com.slh.studentlabhub.data.repository

import android.content.res.AssetManager
import com.slh.studentlabhub.data.reference.ArticleParser
import com.slh.studentlabhub.domain.model.ArticleBlock
import com.slh.studentlabhub.domain.model.ArticleHit
import com.slh.studentlabhub.domain.model.RefArticle
import com.slh.studentlabhub.domain.model.RefArticleLink
import com.slh.studentlabhub.domain.model.RefChapter
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.domain.repository.ArticleRepository
import com.slh.studentlabhub.i18n.Strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class ArticleRepositoryImpl(
    private val assets: AssetManager,
) : ArticleRepository {

    private class LoadedArticle(
        val link: RefArticleLink,
        val chapter: RefChapter,
        val blocks: List<ArticleBlock>,
    )

    private class TechContent(
        val chapters: List<RefChapter>,
        val articles: List<LoadedArticle>,
    )

    private val cache = mutableMapOf<String, TechContent>()
    private val mutex = Mutex()

    override suspend fun chapters(tech: RefTech): List<RefChapter> =
        content(tech).chapters

    override suspend fun article(tech: RefTech, articleId: String): RefArticle? {
        val articles = content(tech).articles
        val index = articles.indexOfFirst { it.link.id == articleId }
        if (index < 0) return null
        val loaded = articles[index]
        return RefArticle(
            link = loaded.link,
            chapter = loaded.chapter,
            blocks = loaded.blocks,
            previous = articles.getOrNull(index - 1)?.link,
            next = articles.getOrNull(index + 1)?.link,
        )
    }

    override suspend fun search(query: String): List<ArticleHit> {
        val needle = query.trim().lowercase()
        if (needle.length < 2) return emptyList()
        val ranked = mutableListOf<Pair<Int, ArticleHit>>()
        RefTech.entries.forEach { tech ->
            content(tech).articles.forEach { article ->
                findHit(article, needle)?.let { ranked += it }
            }
        }
        return ranked.sortedBy { it.first }.map { it.second }
    }

    private fun findHit(article: LoadedArticle, needle: String): Pair<Int, ArticleHit>? {
        val title = article.link.title.lowercase()
        var section: String? = null
        if (title.contains(needle)) {
            val rank = if (title.startsWith(needle)) 0 else 1
            return rank to ArticleHit(article.link, article.chapter.title, null, firstParagraph(article))
        }
        article.blocks.forEach { block ->
            if (block is ArticleBlock.Heading && block.level == 2) section = block.text
            val text = blockText(block)
            val position = text.lowercase().indexOf(needle)
            if (position >= 0) {
                val rank = if (block is ArticleBlock.Heading) 2 else 3
                return rank to ArticleHit(
                    article = article.link,
                    chapterTitle = article.chapter.title,
                    section = section,
                    snippet = snippet(text, position),
                )
            }
        }
        return null
    }

    private fun firstParagraph(article: LoadedArticle): String =
        article.blocks.filterIsInstance<ArticleBlock.Paragraph>().firstOrNull()
            ?.let { snippet(ArticleParser.plainText(it.text), 0) }
            .orEmpty()

    private fun blockText(block: ArticleBlock): String = ArticleParser.plainText(
        when (block) {
            is ArticleBlock.Heading -> block.text
            is ArticleBlock.Paragraph -> block.text
            is ArticleBlock.Code -> block.code
            is ArticleBlock.Output -> block.text
            is ArticleBlock.Note -> block.text
            is ArticleBlock.BulletList -> block.items.joinToString(" ")
            is ArticleBlock.NumberedList -> block.items.joinToString(" ")
            is ArticleBlock.Table -> (listOf(block.header) + block.rows).joinToString(" ") { it.joinToString(" ") }
        },
    ).replace('\n', ' ')

    private fun snippet(text: String, position: Int): String {
        val start = (position - 40).coerceAtLeast(0)
        val end = (position + 80).coerceAtMost(text.length)
        val prefix = if (start > 0) "…" else ""
        val suffix = if (end < text.length) "…" else ""
        return prefix + text.substring(start, end).trim() + suffix
    }

    private suspend fun content(tech: RefTech): TechContent {
        val language = when {
            Strings.isBelarusian() -> LANG_BE
            Strings.isRussian() -> LANG_RU
            else -> LANG_EN
        }
        val key = "${tech.folder}/$language"
        return mutex.withLock {
            cache.getOrPut(key) { withContext(Dispatchers.IO) { load(tech, language) } }
        }
    }

    private fun load(tech: RefTech, language: String): TechContent {
        val toc = readAsset("$ROOT/${tech.folder}/toc.json") ?: return TechContent(emptyList(), emptyList())
        val json = JSONObject(toc)
        val chaptersJson = json.getJSONArray("chapters")
        val chapters = mutableListOf<RefChapter>()
        val articles = mutableListOf<LoadedArticle>()

        for (chapterIndex in 0 until chaptersJson.length()) {
            val chapterJson = chaptersJson.getJSONObject(chapterIndex)
            val titles = chapterJson.getJSONObject("title")
            val ids = chapterJson.getJSONArray("articles")
            val chapterNumber = chapterIndex + 1
            val parsed = (0 until ids.length()).mapNotNull { articleIndex ->
                val id = ids.getString(articleIndex)
                val source = readAsset("$ROOT/${tech.folder}/$language/$id.md")
                    ?: readAsset("$ROOT/${tech.folder}/$LANG_RU/$id.md")
                    ?: return@mapNotNull null
                val article = ArticleParser.parse(source)
                RefArticleLink(
                    id = id,
                    tech = tech,
                    number = "$chapterNumber.${articleIndex + 1}",
                    title = article.title.ifBlank { id },
                ) to article.blocks
            }
            val chapter = RefChapter(
                id = chapterJson.getString("id"),
                tech = tech,
                number = chapterNumber,
                title = titles.optString(language).ifBlank { titles.optString(LANG_RU) },
                articles = parsed.map { it.first },
            )
            chapters += chapter
            parsed.forEach { (link, blocks) -> articles += LoadedArticle(link, chapter, blocks) }
        }
        return TechContent(chapters = chapters, articles = articles)
    }

    private fun readAsset(path: String): String? =
        try {
            assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (_: IOException) {
            null
        }

    private val RefTech.folder: String
        get() = name.lowercase()

    private companion object {
        const val ROOT = "reference"
        const val LANG_RU = "ru"
        const val LANG_BE = "be"
        const val LANG_EN = "en"
    }
}
