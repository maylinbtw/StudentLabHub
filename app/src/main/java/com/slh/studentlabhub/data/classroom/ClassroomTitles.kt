package com.slh.studentlabhub.data.classroom

import com.slh.studentlabhub.domain.model.WorkType

data class ParsedWork(
    val type: WorkType,
    val number: Int?,
    val title: String,
)

object ClassroomTitles {

    private val groupCode = Regex("[a-zа-яё]{1,5}-\\d{1,3}[a-zа-яё]?", RegexOption.IGNORE_CASE)
    private val spaces = Regex("\\s+")
    private val quotes = Regex("[\"«»“”„]")

    private val labKeyword = Regex("(?<![a-zа-яё])(лр|лабораторная\\s+работа|лабораторная|лаб\\.?)(?![a-zа-яё])")
    private val practiceKeyword = Regex("(?<![a-zа-яё])(пр|практическая\\s+работа|практическая|практ\\.?)(?![a-zа-яё])")
    private val numberAfter = Regex("^\\s*(?:№|n|#)?\\s*(\\d{1,3})")
    private val gradePrefix = Regex("^\\s*оценка\\s*\\d*\\s*[_\\-:.]?\\s*")
    private val modulePrefix = Regex("^\\s*м\\d+\\s*[_\\-:.]?\\s*")
    private val edgeJunk = Regex("^[\\s_\\-:.,·|]+|[\\s_\\-:.,·|]+$")

    fun subjectTitle(courseName: String): String {
        val cleaned = courseName
            .replace(quotes, " ")
            .replace(groupCode, " ")
            .replace(spaces, " ")
            .replace(edgeJunk, "")
            .trim()
        return cleaned.ifEmpty { courseName.replace(quotes, "").trim() }
    }

    fun parseWork(taskTitle: String): ParsedWork {
        val original = taskTitle.replace(quotes, " ").replace(spaces, " ").trim()
        val lower = original.lowercase()

        val practice = practiceKeyword.find(lower)
        val lab = labKeyword.find(lower)
        val match = listOfNotNull(lab, practice).minByOrNull { it.range.first }
        if (match == null) {
            return ParsedWork(
                type = WorkType.LAB,
                number = null,
                title = clean(stripPrefixes(original)),
            )
        }

        val type = if (match == practice) WorkType.PRACTICE else WorkType.LAB
        val afterKeyword = match.range.last + 1
        val numberMatch = numberAfter.find(lower.substring(afterKeyword))
        val number = numberMatch?.groupValues?.get(1)?.toIntOrNull()
        val restStart = afterKeyword + (numberMatch?.range?.last?.plus(1) ?: 0)

        val rest = clean(original.substring(restStart.coerceAtMost(original.length)))
        val before = clean(stripPrefixes(original.substring(0, match.range.first)))
        val title = rest.ifEmpty { before }
        return ParsedWork(type = type, number = number, title = title)
    }

    private fun stripPrefixes(text: String): String {
        var result = text
        repeat(2) {
            val lower = result.lowercase()
            gradePrefix.find(lower)?.let { result = result.substring(it.range.last + 1) }
            val lowerAfter = result.lowercase()
            modulePrefix.find(lowerAfter)?.let { result = result.substring(it.range.last + 1) }
        }
        return result
    }

    private fun clean(text: String): String =
        text.replace('_', ' ')
            .replace(spaces, " ")
            .replace(edgeJunk, "")
            .trim()
            .replaceFirstChar { it.uppercase() }
}
