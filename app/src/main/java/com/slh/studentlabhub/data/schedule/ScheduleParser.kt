package com.slh.studentlabhub.data.schedule

import java.time.LocalDate
import java.time.LocalTime

data class ParsedLesson(
    val date: LocalDate,
    val number: Int,
    val start: LocalTime?,
    val end: LocalTime?,
    val subject: String,
    val teacher: String,
    val room: String,
)

data class ParsedSchedule(
    val groups: List<String>,
    /** Даты, на которые в странице есть столбец выбранной группы. */
    val dates: List<LocalDate>,
    val lessons: List<ParsedLesson>,
)

/**
 * Разбор страницы расписания колледжа (таблица Excel, сохранённая как HTML).
 *
 * Одна таблица = один день. Первая строка — номера групп, затем строка с датой,
 * затем пары: по три строки на пару (предмет, преподаватель, аудитория).
 */
object ScheduleParser {

    private val tableRegex = Regex("<table[^>]*>(.*?)</table>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val rowRegex = Regex("<tr[^>]*>(.*?)</tr>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val cellRegex = Regex("<t[dh]([^>]*)>(.*?)</t[dh]>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val rowspanRegex = Regex("rowspan\\s*=\\s*\"?(\\d+)", RegexOption.IGNORE_CASE)
    private val colspanRegex = Regex("colspan\\s*=\\s*\"?(\\d+)", RegexOption.IGNORE_CASE)
    private val brRegex = Regex("<br\\s*/?>", RegexOption.IGNORE_CASE)
    private val tagRegex = Regex("<[^>]+>")
    private val groupRegex = Regex("^[А-ЯЁA-Z]{1,6}-\\d{1,3}[А-ЯЁA-Z]?$")
    private val dateRegex = Regex("(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})")
    private val timeRegex = Regex("(\\d{1,2})[.:](\\d{2})")
    private val subgroupRegex = Regex("(\\d)\\s*п\\s*/\\s*гр\\.?", RegexOption.IGNORE_CASE)
    private val subgroupToken = Regex("§(\\d)§")
    private val hourRegex = Regex("\\s*\\d\\s*час(а|ов)?\\.?", RegexOption.IGNORE_CASE)
    private val wholeGroupRegex = Regex("\\(\\s*об\\.?\\s*гр\\.?\\s*\\)", RegexOption.IGNORE_CASE)

    private class Cell(val text: String, val row: Int, val rowSpan: Int)

    /**
     * @param group номер группы, например «ПЗТ-44»; пустая строка — только список групп.
     * @param subgroup 1 или 2; 0 — показывать пары обеих подгрупп.
     */
    fun parse(html: String, group: String, subgroup: Int): ParsedSchedule {
        val groups = linkedSetOf<String>()
        val dates = mutableListOf<LocalDate>()
        val lessons = mutableListOf<ParsedLesson>()
        val wanted = normalizeGroup(group)

        tableRegex.findAll(html).forEach { tableMatch ->
            val grid = buildGrid(tableMatch.groupValues[1])
            if (grid.isEmpty()) return@forEach

            var groupColumn = -1
            var date: LocalDate? = null
            var r = 0
            while (r < grid.size) {
                val row = grid[r]
                val headerGroups = row.mapIndexedNotNull { col, cell ->
                    val name = cell?.text?.replace(Regex("\\s+"), "").orEmpty()
                    if (cell != null && cell.row == r && groupRegex.matches(name)) col to name else null
                }
                if (headerGroups.isNotEmpty()) {
                    headerGroups.forEach { groups += it.second }
                    groupColumn = headerGroups.firstOrNull { normalizeGroup(it.second) == wanted }?.first ?: -1
                    r++
                    continue
                }

                val first = row.getOrNull(0)
                val distinct = row.filterNotNull().distinct()
                if (distinct.size == 1 && first != null) {
                    dateRegex.find(first.text)?.let { match ->
                        date = runCatching {
                            LocalDate.of(
                                match.groupValues[3].toInt(),
                                match.groupValues[2].toInt(),
                                match.groupValues[1].toInt(),
                            )
                        }.getOrNull()
                        date?.let { if ((groupColumn >= 0 || wanted.isEmpty()) && it !in dates) dates += it }
                    }
                    r++
                    continue
                }

                val number = first?.takeIf { it.row == r }?.text?.trim()?.toIntOrNull()
                val currentDate = date
                if (number == null || currentDate == null) {
                    r++
                    continue
                }

                val blockSize = first.rowSpan.coerceAtLeast(1)
                if (groupColumn >= 0) {
                    val times = timeRegex.findAll(row.getOrNull(1)?.text.orEmpty()).toList()
                    val start = times.firstOrNull()?.let { toTime(it) }
                    val end = times.lastOrNull()?.takeIf { times.size > 1 }?.let { toTime(it) }
                    val subject = textAt(grid, r, groupColumn)
                    val teacher = if (blockSize > 1) textAt(grid, r + 1, groupColumn) else ""
                    val room = if (blockSize > 2) textAt(grid, r + 2, groupColumn) else ""
                    lessonsFor(subject, teacher, room, subgroup).forEach { (name, who, where) ->
                        lessons += ParsedLesson(currentDate, number, start, end, name, who, where)
                    }
                }
                r += blockSize
            }
        }
        return ParsedSchedule(groups = groups.toList(), dates = dates, lessons = lessons)
    }

    fun normalizeGroup(value: String): String =
        value.replace(Regex("\\s+"), "").uppercase()

    private fun toTime(match: MatchResult): LocalTime? = runCatching {
        LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    }.getOrNull()

    /** Текст ячейки, если она начинается в этой строке (а не растянута сверху). */
    private fun textAt(grid: List<List<Cell?>>, row: Int, col: Int): String {
        val cell = grid.getOrNull(row)?.getOrNull(col) ?: return ""
        return if (cell.row == row) cell.text else ""
    }

    private fun lessonsFor(
        subjectText: String,
        teacherText: String,
        roomText: String,
        subgroup: Int,
    ): List<Triple<String, String, String>> {
        if (subjectText.isBlank()) return emptyList()

        val marked = subgroupRegex.replace(subjectText) { "§${it.groupValues[1]}§" }
        val parts = marked.split("/")
            .map { it.replace('\n', ' ').trim() }
            .filter { it.isNotEmpty() }
        val teachers = splitList(teacherText)
        val rooms = splitList(roomText)

        val partSubgroups = parts.map { part -> subgroupToken.find(part)?.groupValues?.get(1)?.toInt() }
        val names = parts.map { cleanName(subgroupToken.replace(it, "")) }

        if (partSubgroups.any { it != null }) {
            val indices = names.indices.filter { i ->
                subgroup == 0 || partSubgroups[i] == null || partSubgroups[i] == subgroup
            }
            return indices.map { i ->
                val label = partSubgroups[i]?.takeIf { subgroup == 0 }?.let { " ($it п/гр)" }.orEmpty()
                Triple(
                    names[i] + label,
                    pick(teachers, i, parts.size),
                    pick(rooms, i, parts.size),
                )
            }
        }

        val name = names.joinToString(" / ")
        val splitBySubgroup = names.size == 1 && teachers.size == 2 && rooms.size == 2 && subgroup in 1..2
        return if (splitBySubgroup) {
            listOf(Triple(name, teachers[subgroup - 1], rooms[subgroup - 1]))
        } else {
            listOf(Triple(name, teachers.joinToString(", "), rooms.joinToString(", ")))
        }
    }

    private fun pick(values: List<String>, index: Int, count: Int): String = when {
        values.size == count -> values[index]
        else -> values.joinToString(", ")
    }

    private fun splitList(text: String): List<String> =
        text.split("/")
            .map { it.replace('\n', ' ').replace(Regex("\\s+"), " ").trim() }
            .filter { it.isNotEmpty() }

    private fun cleanName(text: String): String =
        text.replace(wholeGroupRegex, "")
            .replace(hourRegex, "")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun cellText(html: String): String =
        html.replace(brRegex, "\n")
            .replace(tagRegex, "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace('\u00A0', ' ')
            .lines()
            .map { it.replace(Regex("[ \\t\\r]+"), " ").trim() }
            .filter { it.isNotEmpty() }
            .joinToString("\n")

    private fun buildGrid(tableHtml: String): List<List<Cell?>> {
        val grid = mutableListOf<MutableList<Cell?>>()
        rowRegex.findAll(tableHtml).forEachIndexed { r, rowMatch ->
            while (grid.size <= r) grid.add(mutableListOf())
            var c = 0
            cellRegex.findAll(rowMatch.groupValues[1]).forEach { cellMatch ->
                val attrs = cellMatch.groupValues[1]
                val rowSpan = rowspanRegex.find(attrs)?.groupValues?.get(1)?.toIntOrNull()?.coerceAtLeast(1) ?: 1
                val colSpan = colspanRegex.find(attrs)?.groupValues?.get(1)?.toIntOrNull()?.coerceAtLeast(1) ?: 1
                val row = grid[r]
                while (c < row.size && row[c] != null) c++
                val cell = Cell(cellText(cellMatch.groupValues[2]), r, rowSpan)
                for (dr in 0 until rowSpan) {
                    while (grid.size <= r + dr) grid.add(mutableListOf())
                    val target = grid[r + dr]
                    for (dc in 0 until colSpan) {
                        while (target.size <= c + dc) target.add(null)
                        target[c + dc] = cell
                    }
                }
                c += colSpan
            }
        }
        return grid
    }
}
