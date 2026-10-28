package com.slh.studentlabhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.R
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val timeFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

fun LocalDate.dayLabel(today: LocalDate = LocalDate.now()): String = when (this) {
    today -> Strings.get(R.string.due_today)
    today.plusDays(1) -> Strings.get(R.string.due_tomorrow)
    else -> {
        val locale = Locale.getDefault()
        val day = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
        "$day, ${format(DateTimeFormatter.ofPattern("d MMM", locale))}"
    }
}

fun Lesson.timeLabel(): String {
    val from = start?.format(timeFormatter) ?: return ""
    val to = end?.format(timeFormatter) ?: return from
    return "$from–$to"
}

/** «ср, 28 окт · 2 пара · 10:50 · ауд. 304» */
fun Lesson.shortInfo(today: LocalDate = LocalDate.now()): String = listOfNotNull(
    date.dayLabel(today),
    Strings.get(R.string.lesson_number, number),
    start?.format(timeFormatter),
    room.takeIf { it.isNotBlank() }?.let { Strings.get(R.string.lesson_room, it) },
).joinToString(" · ")

fun List<QueuedWork>.shortList(): String =
    joinToString(", ") { "${it.type.shortPrefix} ${it.number}" }

/** Строка пары для экрана «Сегодня» и расписания. */
@Composable
fun LessonRow(
    lesson: Lesson,
    isNow: Boolean,
    queued: List<QueuedWork>,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = horizontalPadding, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(if (isNow) SlhAccent else Color.Transparent),
        )
        Spacer(modifier = Modifier.width(9.dp))
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = lesson.number.toString(),
                color = if (isNow) SlhAccent else SlhContent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Text(
                text = lesson.start?.format(timeFormatter).orEmpty(),
                color = SlhTextMuted,
                fontFamily = SlhMono,
                fontSize = 11.sp,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.subject,
                color = SlhContent,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val details = listOf(
                lesson.room.takeIf { it.isNotBlank() }?.let { Strings.get(R.string.lesson_room, it) },
                lesson.teacher.takeIf { it.isNotBlank() },
            ).filterNotNull().joinToString(" · ")
            if (details.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = details,
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (queued.isNotEmpty()) {
                Spacer(modifier = Modifier.height(5.dp))
                DefenseChip(text = Strings.get(R.string.defense_bring, queued.shortList()))
            }
        }
    }
}

@Composable
fun DefenseChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SlhAccent.copy(alpha = 0.15f))
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            color = SlhAccent,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Карточка «Следующая пара» на экранах предмета и работы. */
@Composable
fun NextLessonCard(
    lesson: Lesson?,
    queued: List<QueuedWork>,
    isConfigured: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = Strings.get(R.string.next_lesson_caps),
            color = SlhTextMuted,
            fontFamily = SlhMono,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when {
                lesson != null -> lesson.shortInfo()
                isConfigured -> Strings.get(R.string.next_lesson_none)
                else -> Strings.get(R.string.next_lesson_not_configured)
            },
            color = if (lesson != null) SlhContent else SlhTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (lesson != null && queued.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            DefenseChip(text = Strings.get(R.string.defense_bring, queued.shortList()))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390)
@Composable
private fun ScheduleViewsPreview() {
    StudentLabHubTheme {
        val lesson = Lesson(
            id = 1,
            date = LocalDate.now(),
            number = 2,
            start = LocalTime.of(10, 50),
            end = LocalTime.of(12, 30),
            subject = "Технология разработки программного обеспечения",
            teacher = "Кизер О.И.",
            room = "206",
        )
        val queued = listOf(QueuedWork(1, 1, "ТРПО", null, WorkType.LAB, 5, ""))
        Column(modifier = Modifier.padding(16.dp)) {
            LessonRow(lesson = lesson, isNow = true, queued = queued)
            Spacer(modifier = Modifier.height(12.dp))
            NextLessonCard(lesson = lesson, queued = queued, isConfigured = true)
        }
    }
}
