package com.slh.studentlabhub.domain.model

sealed interface ArticleBlock {
    data class Heading(val level: Int, val text: String) : ArticleBlock
    data class Paragraph(val text: String) : ArticleBlock
    data class Code(val language: String, val code: String) : ArticleBlock
    data class Output(val text: String) : ArticleBlock
    data class Note(val kind: NoteKind, val text: String) : ArticleBlock
    data class BulletList(val items: List<String>) : ArticleBlock
    data class NumberedList(val items: List<String>) : ArticleBlock
    data class Table(val header: List<String>, val rows: List<List<String>>) : ArticleBlock
}

enum class NoteKind {
    NOTE,
    WARNING,
}

data class RefArticleLink(
    val id: String,
    val tech: RefTech,
    val number: String,
    val title: String,
)

data class RefChapter(
    val id: String,
    val tech: RefTech,
    val number: Int,
    val title: String,
    val articles: List<RefArticleLink>,
)

data class RefArticle(
    val link: RefArticleLink,
    val chapter: RefChapter,
    val blocks: List<ArticleBlock>,
    val previous: RefArticleLink?,
    val next: RefArticleLink?,
) {
    val headings: List<Pair<Int, ArticleBlock.Heading>>
        get() = blocks.withIndex()
            .filter { (_, block) -> block is ArticleBlock.Heading && block.level == 2 }
            .map { (index, block) -> index to block as ArticleBlock.Heading }
}

data class ArticleHit(
    val article: RefArticleLink,
    val chapterTitle: String,
    val section: String?,
    val snippet: String,
)
