package com.slh.studentlabhub.data.reference

import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

data class ParsedTldrPage(
    val title: String,
    val description: String,
    val example: String,
)

object TldrParser {

    private const val MAX_EXAMPLES = 8

    private val skippedDescriptionMarkers = listOf(
        "more information",
        "дополнительная информация",
        "больше информации",
        "подробнее",
        "http://",
        "https://",
    )

    fun parse(command: String, markdown: String): ParsedTldrPage {
        var title = command
        val descriptionLines = mutableListOf<String>()
        val examples = mutableListOf<String>()
        var pendingDescription: String? = null

        markdown.lines().forEach { rawLine ->
            val line = rawLine.trim()
            when {
                line.startsWith("# ") -> {
                    title = line.removePrefix("# ").trim()
                }

                line.startsWith("> ") -> {
                    val text = line.removePrefix("> ").trim()
                    val lower = text.lowercase()
                    if (skippedDescriptionMarkers.none { lower.contains(it) }) {
                        descriptionLines += cleanText(text)
                    }
                }

                line.startsWith("- ") -> {
                    pendingDescription = cleanText(line.removePrefix("- ").trim().removeSuffix(":"))
                }

                line.length > 1 && line.startsWith("`") && line.endsWith("`") -> {
                    val code = cleanCode(line.substring(1, line.length - 1))
                    val caption = pendingDescription
                    examples += if (caption.isNullOrBlank()) code else "$caption\n$code"
                    pendingDescription = null
                }
            }
        }

        return ParsedTldrPage(
            title = title,
            description = descriptionLines.joinToString(" ").ifBlank { Strings.get(R.string.tldr_default_description) },
            example = examples.take(MAX_EXAMPLES).joinToString("\n\n").ifBlank { command },
        )
    }

    private fun cleanText(text: String): String =
        text.replace("`", "")
            .replace("[", "")
            .replace("]", "")
            .trim()

    private fun cleanCode(code: String): String =
        code.replace("{{", "")
            .replace("}}", "")
            .trim()
}
