package com.slh.studentlabhub.data.schedule

import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork

object DefenseQueue {

    /** Для каждой отмеченной работы — ближайшая пара её предмета, которая ещё не закончилась. */
    fun assign(
        lessons: List<Lesson>,
        queued: List<QueuedWork>,
        now: Long,
    ): Map<Long, List<QueuedWork>> {
        val upcoming = lessons
            .filter { it.endMillis() > now }
            .sortedWith(compareBy({ it.date }, { it.number }))
        val result = linkedMapOf<Long, MutableList<QueuedWork>>()
        queued.forEach { work ->
            val lesson = upcoming.firstOrNull { SubjectMatcher.matches(work, it) } ?: return@forEach
            result.getOrPut(lesson.id) { mutableListOf() }.add(work)
        }
        return result.mapValues { (_, works) -> works.sortedWith(compareBy({ it.type.ordinal }, { it.number })) }
    }

    fun nextLesson(
        lessons: List<Lesson>,
        subjectTitle: String,
        scheduleTitle: String?,
        now: Long,
    ): Lesson? =
        lessons
            .filter { it.endMillis() > now && SubjectMatcher.matches(subjectTitle, scheduleTitle, it.subject) }
            .minWithOrNull(compareBy({ it.date }, { it.number }))
}
