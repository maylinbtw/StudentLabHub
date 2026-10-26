package com.slh.studentlabhub.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.ui.auth.rememberGoogleAuthorization
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhPullRefresh
import com.slh.studentlabhub.ui.components.daysUntil
import com.slh.studentlabhub.ui.components.toLocalDate
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.time.YearMonth

private val ruLocale: Locale
    get() = Locale.getDefault()
private val headerFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("EEEE, d MMMM", ruLocale)
private val timeFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("HH:mm", ruLocale)
private val dateFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("dd.MM", ruLocale)
private val syncFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("dd.MM HH:mm", ruLocale)

private const val LATER_LIMIT = 8

private fun YearMonth.toIndex(): Int = year * 12 + monthValue - 1

private fun Int.toYearMonth(): YearMonth = YearMonth.of(this / 12, this % 12 + 1)

@Composable
fun TodayRoute(
    onOpenLab: (labId: Long) -> Unit,
    onOpenSubjects: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = viewModel(factory = TodayViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val authorize = rememberGoogleAuthorization(
        onAuthorized = viewModel::sync,
        onError = viewModel::onAuthorizationFailed,
    )
    val onSync = {
        if (!state.isSyncing) {
            viewModel.onSyncStarted()
            authorize()
        }
    }

    TodayScreen(
        state = state,
        onSyncClick = onSync,
        onRowClick = { row -> onOpenLab(row.labId) },
        onOpenSubjects = onOpenSubjects,
        modifier = modifier,
    )
}

@Composable
fun TodayScreen(
    state: TodayUiState,
    onSyncClick: () -> Unit,
    onRowClick: (AgendaRow) -> Unit,
    onOpenSubjects: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var calendarMode by rememberSaveable { mutableStateOf(false) }
    var monthIndex by rememberSaveable { mutableStateOf(YearMonth.now().toIndex()) }
    var selectedDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    val calendarMonth = monthIndex.toYearMonth()
    val selectedDate = LocalDate.ofEpochDay(selectedDay)
    val deadlinesByDate = remember(state.calendarRows) { state.calendarRows.groupByDueDate() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = state.today.format(headerFormatter).replaceFirstChar { it.uppercase() },
            title = Strings.get(R.string.today_title),
            actionIcon = SlhIcon.Refresh,
            onActionClick = onSyncClick,
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = SlhAccent,
                    strokeWidth = 2.dp,
                )
            }
            return@Column
        }

        SlhPullRefresh(
            isRefreshing = state.isSyncing,
            onRefresh = onSyncClick,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                item(key = "summary") {
                    Summary(state = state)
                    Spacer(modifier = Modifier.height(18.dp))
                }

                if (state.hasSubjects) {
                    item(key = "mode") {
                        TodayModeSwitch(
                            isCalendar = calendarMode,
                            onChange = { calendarMode = it },
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                if (state.hasSubjects && calendarMode) {
                    item(key = "calendar") {
                        DeadlineCalendar(
                            month = calendarMonth,
                            today = state.today,
                            selected = selectedDate,
                            deadlines = deadlinesByDate,
                            onMonthChange = { monthIndex = it.toIndex() },
                            onSelect = { selectedDay = it.toEpochDay() },
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    val dayRows = deadlinesByDate[selectedDate].orEmpty()
                    item(key = "day_title") {
                        CalendarDayTitle(date = selectedDate, count = dayRows.size)
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    if (dayRows.isEmpty()) {
                        item(key = "day_empty") { CalendarDayEmpty() }
                    }
                    items(items = dayRows, key = { "day_row_${it.labId}" }) { row ->
                        AgendaRowItem(
                            row = row,
                            now = state.now,
                            onClick = { onRowClick(row) },
                        )
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    return@LazyColumn
                }

                if (!state.hasSubjects) {
                    item(key = "welcome") {
                        WelcomeCard(
                            isSyncing = state.isSyncing,
                            onSyncClick = onSyncClick,
                            onOpenSubjects = onOpenSubjects,
                        )
                    }
                    return@LazyColumn
                }

                if (state.sections.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = Strings.get(R.string.today_empty),
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                        )
                    }
                }

                state.sections.forEach { section ->
                    val visible = if (section.group == AgendaGroup.LATER) {
                        section.rows.take(LATER_LIMIT)
                    } else {
                        section.rows
                    }
                    item(key = "label_${section.group.name}") {
                        SectionLabel(text = Strings.get(R.string.section_with_count, section.group.title, section.rows.size))
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    items(items = visible, key = { "row_${section.group.name}_${it.labId}" }) { row ->
                        AgendaRowItem(
                            row = row,
                            now = state.now,
                            onClick = { onRowClick(row) },
                        )
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    if (section.rows.size > visible.size) {
                        item(key = "more_${section.group.name}") {
                            Text(
                                text = Strings.get(R.string.today_more_in_subjects, section.rows.size - visible.size),
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 10.dp),
                            )
                        }
                    }
                    item(key = "space_${section.group.name}") {
                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Summary(state: TodayUiState) {
    val parts = buildList {
        if (state.overdueCount > 0) add(Strings.get(R.string.summary_overdue, state.overdueCount))
        if (state.redoCount > 0) add(Strings.get(R.string.summary_redo, state.redoCount))
        if (state.todayCount > 0) add(Strings.get(R.string.summary_today, state.todayCount))
        if (state.weekCount > 0) add(Strings.get(R.string.summary_week, state.weekCount))
    }
    Text(
        text = if (parts.isEmpty()) Strings.get(R.string.all_under_control) else parts.joinToString(" · "),
        color = if (state.overdueCount > 0 || state.redoCount > 0) SlhDanger else SlhContent,
        fontFamily = SlhMono,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = when {
            state.isSyncing -> Strings.get(R.string.classroom_syncing)
            state.lastSyncAt != null -> Strings.get(R.string.classroom_updated_at, state.lastSyncAt.formatWith(syncFormatter))
            else -> Strings.get(R.string.classroom_not_synced_pull)
        },
        color = SlhTextMuted,
        style = MaterialTheme.typography.bodySmall,
    )
    state.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            color = SlhDanger,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun AgendaRowItem(
    row: AgendaRow,
    now: Long,
    onClick: () -> Unit,
) {
    val (label, labelColor) = dueLabel(row, now)
    val dotColor = when (row.group) {
        AgendaGroup.REDO, AgendaGroup.OVERDUE -> SlhDanger
        AgendaGroup.TODAY -> SlhAccent
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.title,
                color = SlhContent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = row.subjectTitle,
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = labelColor,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

private fun dueLabel(row: AgendaRow, now: Long): Pair<String, Color> {
    val due = row.dueAt
    return when (row.group) {
        AgendaGroup.REDO -> Strings.get(R.string.redo_short) to SlhDanger
        AgendaGroup.OVERDUE -> {
            val days = if (due == null) 0L else -daysUntil(due, now)
            (if (days <= 0L) Strings.get(R.string.overdue) else Strings.get(R.string.overdue_days_short, days.toInt())) to SlhDanger
        }
        AgendaGroup.TODAY -> Strings.get(R.string.until_time, due?.formatWith(timeFormatter).orEmpty()) to SlhAccent
        AgendaGroup.TOMORROW -> (due?.formatWith(timeFormatter).orEmpty()) to SlhTextSecondary
        AgendaGroup.WEEK -> (due?.toLocalDate()?.dayOfWeek?.getDisplayName(TextStyle.SHORT, ruLocale).orEmpty()) to SlhTextSecondary
        AgendaGroup.LATER -> (due?.formatWith(dateFormatter).orEmpty()) to SlhTextMuted
    }
}

private fun Long.formatWith(formatter: DateTimeFormatter): String =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(formatter)

@Composable
private fun WelcomeCard(
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
    onOpenSubjects: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .padding(20.dp),
    ) {
        DotMatrixText(text = "SLH", color = SlhAccent, dotSize = 5.dp, dotGap = 1.5.dp)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = Strings.get(R.string.where_to_start),
            color = SlhContent,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = Strings.get(R.string.welcome_text),
            color = SlhTextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSyncing) SlhOutline else SlhAccent)
                .clickable(enabled = !isSyncing, role = Role.Button, onClick = onSyncClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isSyncing) Strings.get(R.string.loading_caps) else Strings.get(R.string.load_from_classroom),
                color = if (isSyncing) SlhTextMuted else SlhOnAccent,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = Strings.get(R.string.add_subjects_manually_link),
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable(role = Role.Button, onClick = onOpenSubjects)
                .padding(vertical = 10.dp),
        )
    }
}

private const val PREVIEW_NOW = 1_792_900_000_000L

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 760)
@Composable
private fun TodayScreenPreview() {
    StudentLabHubTheme {
        TodayScreen(
            state = TodayUiState(
                isLoading = false,
                now = PREVIEW_NOW,
                today = LocalDate.of(2026, 10, 26),
                sections = listOf(
                    AgendaSection(
                        AgendaGroup.OVERDUE,
                        listOf(AgendaRow(1, "ЛР 8 · Реализация алгоритма", "ЗКИ", PREVIEW_NOW - 2 * 86_400_000, AgendaGroup.OVERDUE)),
                    ),
                    AgendaSection(
                        AgendaGroup.TODAY,
                        listOf(AgendaRow(2, "ПР 4 · Решение производственных задач", "ММ", PREVIEW_NOW + 3_600_000, AgendaGroup.TODAY)),
                    ),
                    AgendaSection(
                        AgendaGroup.WEEK,
                        listOf(
                            AgendaRow(3, "ЛР 20 · Изучение системы контроля версий", "ТРПО", PREVIEW_NOW + 5 * 86_400_000, AgendaGroup.WEEK),
                            AgendaRow(4, "ЛР 18 · UI-прототипирование", "ТРПО", PREVIEW_NOW + 5 * 86_400_000, AgendaGroup.WEEK),
                        ),
                    ),
                ),
                overdueCount = 1,
                todayCount = 1,
                weekCount = 2,
                lastSyncAt = PREVIEW_NOW,
            ),
            onSyncClick = {},
            onRowClick = {},
            onOpenSubjects = {},
        )
    }
}
