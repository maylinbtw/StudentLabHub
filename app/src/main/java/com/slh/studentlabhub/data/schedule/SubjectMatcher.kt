package com.slh.studentlabhub.data.schedule

import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.model.SubjectRef

/**
 * Сопоставляет предмет приложения («ТРПО») с названием пары в расписании
 * («Технология разработки программного обеспечения»).
 */
object SubjectMatcher {

    private val stopWords = setOf("и", "в", "во", "на", "для", "с", "со", "по", "о", "об", "к", "из", "от", "а")

    fun matches(subjectTitle: String, scheduleTitle: String?, lessonSubject: String): Boolean {
        val lesson = normalize(lessonSubject)
        if (lesson.isEmpty()) return false

        scheduleTitle?.takeIf { it.isNotBlank() }?.let { manual ->
            return normalize(manual) == lesson
        }

        val subject = normalize(subjectTitle)
        if (subject.isEmpty()) return false
        if (subject == lesson) return true

        val compact = subject.replace(" ", "")
        if (!subject.contains(' ') && compact.length in 2..7 && compact == abbreviation(lesson)) return true

        val shortest = minOf(subject.length, lesson.length)
        return shortest >= 6 && (lesson.startsWith(subject) || subject.startsWith(lesson))
    }

    fun matches(subject: SubjectRef, lesson: Lesson): Boolean =
        matches(subject.title, subject.scheduleTitle, lesson.subject)

    fun matches(work: QueuedWork, lesson: Lesson): Boolean =
        matches(work.subjectTitle, work.scheduleTitle, lesson.subject)

    fun normalize(value: String): String =
        value.lowercase()
            .replace('ё', 'е')
            .replace(Regex("\\(.*?\\)"), " ")
            .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
            .trim()

    fun abbreviation(normalized: String): String =
        normalized.split(' ')
            .filter { it.isNotEmpty() && it !in stopWords }
            .joinToString("") { it.first().toString() }
}
