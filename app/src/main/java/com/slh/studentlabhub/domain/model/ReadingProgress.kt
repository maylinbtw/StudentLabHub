package com.slh.studentlabhub.domain.model

data class ReadingProgress(
    val read: Set<String> = emptySet(),
    val bookmarks: Set<String> = emptySet(),
    val lastArticle: String? = null,
    val fontStep: Int = DEFAULT_FONT_STEP,
) {
    val fontScale: Float
        get() = FONT_SCALES[fontStep.coerceIn(FONT_SCALES.indices)]

    fun isRead(link: RefArticleLink): Boolean = link.key in read

    fun isBookmarked(link: RefArticleLink): Boolean = link.key in bookmarks

    fun readCount(chapter: RefChapter): Int = chapter.articles.count { isRead(it) }

    companion object {
        val FONT_SCALES = listOf(0.9f, 1f, 1.15f, 1.3f, 1.45f)
        const val DEFAULT_FONT_STEP = 1
    }
}

val RefArticleLink.key: String
    get() = articleKey(tech, id)

fun articleKey(tech: RefTech, id: String): String = "${tech.name}/$id"

fun parseArticleKey(key: String): Pair<RefTech, String>? {
    val tech = RefTech.entries.firstOrNull { it.name == key.substringBefore('/') } ?: return null
    val id = key.substringAfter('/', "").takeIf { it.isNotBlank() } ?: return null
    return tech to id
}
