package com.slh.studentlabhub.data.reference

import com.slh.studentlabhub.domain.model.ArticleBlock
import com.slh.studentlabhub.domain.model.NoteKind

object ArticleParser {

    data class Parsed(
        val title: String,
        val blocks: List<ArticleBlock>,
    )

    private val numberedRegex = Regex("^\\d+\\.\\s+")
    private val tableSeparatorRegex = Regex("^\\|?\\s*:?-{2,}.*$")

    fun parse(source: String): Parsed {
        val lines = source.replace("\r\n", "\n").lines()
        val blocks = mutableListOf<ArticleBlock>()
        var title = ""
        val paragraph = mutableListOf<String>()
        var i = 0

        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += ArticleBlock.Paragraph(paragraph.joinToString(" "))
                paragraph.clear()
            }
        }

        while (i < lines.size) {
            val line = lines[i]
            val trimmed = line.trim()
            when {
                trimmed.isEmpty() -> {
                    flushParagraph()
                    i++
                }

                trimmed.startsWith("```") -> {
                    flushParagraph()
                    val language = trimmed.removePrefix("```").trim().lowercase()
                    val code = mutableListOf<String>()
                    i++
                    while (i < lines.size && !lines[i].trim().startsWith("```")) {
                        code += lines[i]
                        i++
                    }
                    i++
                    val text = code.joinToString("\n").trimEnd()
                    blocks += if (language == "output") {
                        ArticleBlock.Output(text)
                    } else {
                        ArticleBlock.Code(language, text)
                    }
                }

                trimmed.startsWith("# ") && title.isEmpty() -> {
                    title = trimmed.removePrefix("# ").trim()
                    i++
                }

                trimmed.startsWith("### ") -> {
                    flushParagraph()
                    blocks += ArticleBlock.Heading(3, trimmed.removePrefix("### ").trim())
                    i++
                }

                trimmed.startsWith("## ") -> {
                    flushParagraph()
                    blocks += ArticleBlock.Heading(2, trimmed.removePrefix("## ").trim())
                    i++
                }

                trimmed.startsWith(">") -> {
                    flushParagraph()
                    val quoted = mutableListOf<String>()
                    while (i < lines.size && lines[i].trim().startsWith(">")) {
                        quoted += lines[i].trim().removePrefix(">").trim()
                        i++
                    }
                    var kind = NoteKind.NOTE
                    if (quoted.firstOrNull()?.uppercase() == "[!WARNING]") {
                        kind = NoteKind.WARNING
                        quoted.removeAt(0)
                    } else if (quoted.firstOrNull()?.uppercase() == "[!NOTE]") {
                        quoted.removeAt(0)
                    }
                    blocks += ArticleBlock.Note(kind, joinLines(quoted))
                }

                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    flushParagraph()
                    val items = mutableListOf<String>()
                    while (i < lines.size) {
                        val item = lines[i].trim()
                        if (item.startsWith("- ") || item.startsWith("* ")) {
                            items += item.substring(2).trim()
                        } else if (item.isNotEmpty() && lines[i].startsWith("  ") && items.isNotEmpty()) {
                            items[items.lastIndex] = items.last() + " " + item
                        } else {
                            break
                        }
                        i++
                    }
                    blocks += ArticleBlock.BulletList(items)
                }

                numberedRegex.containsMatchIn(trimmed) -> {
                    flushParagraph()
                    val items = mutableListOf<String>()
                    while (i < lines.size) {
                        val item = lines[i].trim()
                        if (numberedRegex.containsMatchIn(item)) {
                            items += item.replaceFirst(numberedRegex, "")
                        } else if (item.isNotEmpty() && lines[i].startsWith("  ") && items.isNotEmpty()) {
                            items[items.lastIndex] = items.last() + " " + item
                        } else {
                            break
                        }
                        i++
                    }
                    blocks += ArticleBlock.NumberedList(items)
                }

                trimmed.startsWith("|") -> {
                    flushParagraph()
                    val rows = mutableListOf<List<String>>()
                    while (i < lines.size && lines[i].trim().startsWith("|")) {
                        val row = lines[i].trim()
                        if (!tableSeparatorRegex.matches(row)) {
                            rows += splitRow(row)
                        }
                        i++
                    }
                    if (rows.isNotEmpty()) {
                        blocks += ArticleBlock.Table(header = rows.first(), rows = rows.drop(1))
                    }
                }

                else -> {
                    paragraph += trimmed
                    i++
                }
            }
        }
        flushParagraph()
        return Parsed(title = title, blocks = blocks)
    }

    private fun joinLines(lines: List<String>): String =
        lines.joinToString("\n") { it }
            .split("\n\n")
            .joinToString("\n") { part -> part.lines().filter { it.isNotBlank() }.joinToString(" ") }
            .trim()

    private fun splitRow(row: String): List<String> {
        val cells = mutableListOf<String>()
        val current = StringBuilder()
        var inCode = false
        val body = row.trim().removePrefix("|").removeSuffix("|")
        var index = 0
        while (index < body.length) {
            val char = body[index]
            when {
                char == '\\' && index + 1 < body.length && body[index + 1] == '|' -> {
                    current.append('|')
                    index++
                }
                char == '`' -> {
                    inCode = !inCode
                    current.append(char)
                }
                char == '|' && !inCode -> {
                    cells += current.toString().trim()
                    current.clear()
                }
                else -> current.append(char)
            }
            index++
        }
        cells += current.toString().trim()
        return cells
    }

    fun plainText(text: String): String =
        text.replace("**", "").replace("`", "")
}
