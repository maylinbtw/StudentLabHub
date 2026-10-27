package com.slh.studentlabhub.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class Lesson(
    val id: Long,
    val date: LocalDate,
    val number: Int,
    val start: LocalTime?,
    val end: LocalTime?,
    val subject: String,
    val teacher: String,
    val room: String,
) {
    fun startMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
        LocalDateTime.of(date, start ?: LocalTime.of(8, 0)).atZone(zone).toInstant().toEpochMilli()

    fun endMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
        LocalDateTime.of(date, end ?: start?.plusMinutes(95) ?: LocalTime.of(23, 59))
            .atZone(zone).toInstant().toEpochMilli()
}

/** Работа, которую студент отметил «нести на защиту». */
data class QueuedWork(
    val labId: Long,
    val subjectId: Long,
    val subjectTitle: String,
    val scheduleTitle: String?,
    val type: WorkType,
    val number: Int,
    val title: String,
)

/** Ссылка на предмет для сопоставления с расписанием. */
data class SubjectRef(
    val id: Long,
    val title: String,
    val scheduleTitle: String?,
)

data class ScheduleRefreshResult(
    val newDates: List<LocalDate>,
    val changedDates: List<LocalDate>,
)

enum class ScheduleSubgroup(val value: Int) {
    BOTH(0),
    FIRST(1),
    SECOND(2),
}
