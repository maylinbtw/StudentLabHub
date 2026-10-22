package com.slh.studentlabhub.domain.model

enum class RefTech(val label: String) {
    LINUX("Linux"),
    CPP("C++"),
    JAVA("Java"),
    PYTHON("Python"),
}

enum class RefSource {
    BUILTIN,
    CUSTOM,
    TLDR,
}

data class RefCategory(
    val id: String,
    val tech: RefTech,
    val title: String,
    val entryTitles: List<String>,
)

data class RefEntry(
    val id: String,
    val tech: RefTech,
    val categoryId: String,
    val categoryTitle: String,
    val title: String,
    val description: String,
    val example: String,
    val source: RefSource = RefSource.BUILTIN,
) {
    val isEditable: Boolean
        get() = source != RefSource.BUILTIN
}

data class ReferenceData(
    val categories: List<RefCategory>,
    val entries: List<RefEntry>,
)

data class RefEntryDraft(
    val id: String?,
    val tech: RefTech,
    val categoryId: String?,
    val title: String,
    val description: String,
    val example: String,
)

sealed interface TldrResult {
    data class Saved(val title: String) : TldrResult
    data object NotFound : TldrResult
    data class Failed(val message: String) : TldrResult
}

object RefCategoryIds {
    const val TLDR_LINUX = "tldr_linux"

    fun custom(tech: RefTech): String = "custom_${tech.name.lowercase()}"
}

private val tldrCommandRegex = Regex("^[a-zA-Z0-9._+-]+( [a-zA-Z0-9._+-]+)?$")

fun tldrCommandOrNull(query: String): String? {
    val trimmed = query.trim()
    if (!tldrCommandRegex.matches(trimmed)) return null
    return trimmed.lowercase().replace(' ', '-')
}

fun List<RefEntry>.search(query: String): List<RefEntry> {
    val needle = query.trim().lowercase()
    if (needle.isEmpty()) return emptyList()
    return filter { entry ->
        entry.title.lowercase().contains(needle) ||
            entry.description.lowercase().contains(needle) ||
            entry.example.lowercase().contains(needle) ||
            entry.categoryTitle.lowercase().contains(needle)
    }.sortedBy { entry ->
        val title = entry.title.lowercase()
        when {
            title == needle -> 0
            title.startsWith(needle) -> 1
            title.contains(needle) -> 2
            else -> 3
        }
    }
}
