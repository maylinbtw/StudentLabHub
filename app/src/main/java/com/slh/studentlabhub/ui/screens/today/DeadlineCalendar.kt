package com.slh.studentlabhub.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.components.toLocalDate
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale
import androidx.compose.ui.text.style.TextAlign
import java.time.format.DateTimeFormatter
import com.slh.studentlabhub.ui.theme.SlhOnContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput

private const val URGENT_DAYS = 3L
private const val SWIPE_THRESHOLD_PX = 120f

private enum class DayKind { NONE, OVERDUE, URGENT, LATER }

fun List<AgendaRow>.groupByDueDate(): Map<LocalDate, List<AgendaRow>> =
    filter { it.dueAt != null }.groupBy { it.dueAt!!.toLocalDate() }

@Composable
fun TodayModeSwitch(
    isCalendar: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .padding(3.dp),
    ) {
        ModeOption(
            text = Strings.get(R.string.today_mode_list),
            selected = !isCalendar,
            onClick = { onChange(false) },
            modifier = Modifier.weight(1f),
        )
        ModeOption(
            text = Strings.get(R.string.today_mode_calendar),
            selected = isCalendar,
            onClick = { onChange(true) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ModeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) SlhContent else Color.Transparent)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) SlhOnContent else SlhTextSecondary,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun DeadlineCalendar(
    month: YearMonth,
    today: LocalDate,
    selected: LocalDate,
    deadlines: Map<LocalDate, List<AgendaRow>>,
    onMonthChange: (YearMonth) -> Unit,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = Locale.getDefault()
    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
    val weekDays = List(7) { firstDayOfWeek.plus(it.toLong()) }
    val leadingBlanks = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val cells: List<LocalDate?> = buildList {
        repeat(leadingBlanks) { add(null) }
        for (day in 1..month.lengthOfMonth()) add(month.atDay(day))
        while (size % 7 != 0) add(null)
    }

    var dragTotal by remember { mutableFloatStateOf(0f) }
    val swipeModifier = Modifier.pointerInput(month) {
        detectHorizontalDragGestures(
            onDragStart = { dragTotal = 0f },
            onDragEnd = {
                when {
                    dragTotal > SWIPE_THRESHOLD_PX -> onMonthChange(month.minusMonths(1))
                    dragTotal < -SWIPE_THRESHOLD_PX -> onMonthChange(month.plusMonths(1))
                }
            },
            onHorizontalDrag = { _, amount -> dragTotal += amount },
        )
    }

    Column(modifier = modifier.fillMaxWidth().then(swipeModifier)) {
        MonthHeader(
            month = month,
            locale = locale,
            onPrevious = { onMonthChange(month.minusMonths(1)) },
            onNext = { onMonthChange(month.plusMonths(1)) },
            onToday = if (month != YearMonth.from(today)) {
                {
                    onMonthChange(YearMonth.from(today))
                    onSelect(today)
                }
            } else {
                null
            },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT_STANDALONE, locale)
                        .take(2)
                        .uppercase(locale),
                    color = if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) SlhTextMuted else SlhTextSecondary,
                    fontFamily = SlhMono,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (date != null) {
                            val rows = deadlines[date].orEmpty()
                            DayCell(
                                date = date,
                                count = rows.size,
                                kind = dayKind(date, today, rows),
                                isToday = date == today,
                                isSelected = date == selected,
                                onClick = { onSelect(date) },
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Legend()
    }
}

private fun dayKind(date: LocalDate, today: LocalDate, rows: List<AgendaRow>): DayKind {
    if (rows.isEmpty()) return DayKind.NONE
    val days = ChronoUnit.DAYS.between(today, date)
    return when {
        days < 0 -> DayKind.OVERDUE
        days <= URGENT_DAYS -> DayKind.URGENT
        else -> DayKind.LATER
    }
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    locale: Locale,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowButton(text = "‹", onClick = onPrevious)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)} ${month.year}"
                    .uppercase(locale),
                color = SlhContent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.5.sp,
            )
            if (onToday != null) {
                Text(
                    text = Strings.get(R.string.calendar_back_to_today),
                    color = SlhAccent,
                    fontFamily = SlhMono,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(role = Role.Button, onClick = onToday)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
        }
        ArrowButton(text = "›", onClick = onNext)
    }
}

@Composable
private fun ArrowButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SlhOutline, RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SlhContent,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    count: Int,
    kind: DayKind,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val background = when (kind) {
        DayKind.URGENT -> SlhDanger
        DayKind.OVERDUE -> SlhDanger.copy(alpha = 0.18f)
        DayKind.LATER -> SlhSurface
        DayKind.NONE -> Color.Transparent
    }
    val textColor = when (kind) {
        DayKind.URGENT -> Color.White
        DayKind.OVERDUE -> SlhDanger
        DayKind.LATER -> SlhContent
        DayKind.NONE -> if (isToday) SlhContent else SlhTextSecondary
    }
    val dotColor = when (kind) {
        DayKind.URGENT -> Color.White
        DayKind.OVERDUE -> SlhDanger
        else -> SlhAccent
    }
    val borderColor = when {
        isSelected -> SlhContent
        isToday -> SlhAccent
        kind == DayKind.LATER -> SlhOutline
        else -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(background)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                fontFamily = SlhMono,
                fontWeight = if (isToday || kind != DayKind.NONE) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
            )
            if (count > 0) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(count.coerceAtMost(3)) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(dotColor),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendItem(color = SlhDanger, text = Strings.get(R.string.calendar_legend_urgent))
        LegendItem(color = SlhDanger.copy(alpha = 0.18f), text = Strings.get(R.string.calendar_legend_overdue))
        LegendItem(color = SlhSurface, border = SlhOutline, text = Strings.get(R.string.calendar_legend_later))
    }
}

@Composable
private fun LegendItem(color: Color, text: String, border: Color = Color.Transparent) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(1.dp, border, RoundedCornerShape(3.dp)),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = SlhTextMuted,
            fontSize = 11.sp,
        )
    }
}

@Composable
fun CalendarDayTitle(date: LocalDate, count: Int) {
    val locale = Locale.getDefault()
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    val monthDay = DateTimeFormatter.ofPattern("d MMMM", locale).format(date)
    Text(
        text = "$dayName, $monthDay · $count".uppercase(locale),
        color = if (count > 0) SlhContent else SlhTextMuted,
        fontFamily = SlhMono,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@Composable
fun CalendarDayEmpty() {
    Text(
        text = Strings.get(R.string.calendar_day_empty),
        color = SlhTextDisabled,
        fontSize = 13.sp,
        modifier = Modifier.padding(vertical = 14.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390)
@Composable
private fun DeadlineCalendarPreview() {
    StudentLabHubTheme {
        val today = LocalDate.of(2026, 10, 26)
        val rows = listOf(
            AgendaRow(1, "ЛР 8", "ЗКИ", null, AgendaGroup.OVERDUE),
        )
        Column(modifier = Modifier.padding(16.dp)) {
            TodayModeSwitch(isCalendar = true, onChange = {})
            Spacer(modifier = Modifier.height(16.dp))
            DeadlineCalendar(
                month = YearMonth.from(today),
                today = today,
                selected = today,
                deadlines = mapOf(
                    today.minusDays(2) to rows,
                    today to rows,
                    today.plusDays(1) to rows + rows,
                    today.plusDays(9) to rows,
                ),
                onMonthChange = {},
                onSelect = {},
            )
        }
    }
}
