package com.slh.studentlabhub.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.AgendaItem
import com.slh.studentlabhub.domain.model.AgendaSource
import com.slh.studentlabhub.ui.auth.rememberGoogleAuthorization
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.LinkBanner
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhProgressDots
import com.slh.studentlabhub.ui.components.SlhPullRefresh
import com.slh.studentlabhub.ui.components.daysUntil
import com.slh.studentlabhub.ui.components.relativeDueLabel
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhDotShape
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ruLocale: Locale = Locale.forLanguageTag("ru")
private val headerFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", ruLocale)
private val dayTitleFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM, EEEE", ruLocale)
private val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", ruLocale)
private val syncFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm", ruLocale)

private const val UPCOMING_COLLAPSED = 5

@Composable
fun TodayRoute(
    onOpenSubject: (subjectId: Long) -> Unit,
    onOpenCourse: (courseId: String) -> Unit,
    onOpenLab: (labId: Long) -> Unit,
    onOpenSubjects: () -> Unit,
    onOpenStatistics: () -> Unit,
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
        onPreviousWeek = viewModel::previousWeek,
        onNextWeek = viewModel::nextWeek,
        onDayClick = viewModel::onDayClick,
        onItemClick = { item ->
            when {
                item.labId != null -> onOpenLab(item.labId)
                item.subjectId != null -> onOpenSubject(item.subjectId)
                item.courseId != null -> onOpenCourse(item.courseId)
            }
        },
        onOpenSubjects = onOpenSubjects,
        onOpenStatistics = onOpenStatistics,
        modifier = modifier,
    )
}

@Composable
fun TodayScreen(
    state: TodayUiState,
    onSyncClick: () -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    onItemClick: (AgendaItem) -> Unit,
    onOpenSubjects: () -> Unit,
    onOpenStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAllUpcoming by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = state.today.format(headerFormatter).replaceFirstChar { it.uppercase() },
            title = "СЕГОДНЯ",
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
                item(key = "sync_status") {
                    SyncStatus(
                        isSyncing = state.isSyncing,
                        lastSyncAt = state.lastSyncAt,
                        errorMessage = state.errorMessage,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.isEmptyStart) {
                    item(key = "welcome") {
                        WelcomeCard(
                            isSyncing = state.isSyncing,
                            onSyncClick = onSyncClick,
                            onOpenSubjects = onOpenSubjects,
                        )
                    }
                    return@LazyColumn
                }

                if (state.unlinkedCourses > 0) {
                    item(key = "unlinked") {
                        LinkBanner(
                            overline = "GOOGLE CLASSROOM",
                            title = "${state.unlinkedCourses} ${coursesWord(state.unlinkedCourses)} без предмета",
                            subtitle = "Добавь их в «Предметы», чтобы вести лабы и оценки",
                            onClick = onOpenSubjects,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item(key = "week") {
                    WeekStrip(
                        state = state,
                        onPreviousWeek = onPreviousWeek,
                        onNextWeek = onNextWeek,
                        onDayClick = onDayClick,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val selected = state.selectedDay
                if (selected != null) {
                    item(key = "day_label") {
                        SectionLabel(
                            text = selected.format(dayTitleFormatter).uppercase(ruLocale) + " · ${state.dayItems.size}",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (state.dayItems.isEmpty()) {
                        item(key = "day_empty") {
                            EmptyLine(text = "В этот день сдавать нечего")
                        }
                    }
                    items(items = state.dayItems, key = { "day_${it.key}" }) { item ->
                        AgendaRow(item = item, now = state.now, onClick = { onItemClick(item) })
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    item(key = "day_space") {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (state.redo.isNotEmpty()) {
                    item(key = "redo_label") {
                        SectionLabel(text = "НА ДОРАБОТКУ · ${state.redo.size}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(items = state.redo, key = { "redo_${it.key}" }) { item ->
                        AgendaRow(item = item, now = state.now, onClick = { onItemClick(item) })
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    item(key = "redo_space") {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item(key = "upcoming_label") {
                    SectionLabel(text = "БЛИЖАЙШИЕ ДЕДЛАЙНЫ · ${state.upcoming.size}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (state.upcoming.isEmpty()) {
                    item(key = "upcoming_empty") {
                        EmptyLine(text = "Дедлайнов нет — можно выдохнуть")
                    }
                }
                val visible = if (showAllUpcoming) state.upcoming else state.upcoming.take(UPCOMING_COLLAPSED)
                items(items = visible, key = { "up_${it.key}" }) { item ->
                    AgendaRow(item = item, now = state.now, onClick = { onItemClick(item) })
                    Spacer(modifier = Modifier.height(6.dp))
                }
                if (state.upcoming.size > UPCOMING_COLLAPSED) {
                    item(key = "upcoming_toggle") {
                        TextAction(
                            text = if (showAllUpcoming) "свернуть" else "показать все (${state.upcoming.size})",
                            onClick = { showAllUpcoming = !showAllUpcoming },
                        )
                    }
                }

                item(key = "summary") {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionLabel(text = "УСПЕВАЕМОСТЬ")
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryCard(
                        average = state.overallAverage,
                        total = state.totalWorks,
                        done = state.doneWorks,
                        defended = state.defendedWorks,
                        onClick = onOpenStatistics,
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncStatus(
    isSyncing: Boolean,
    lastSyncAt: Long?,
    errorMessage: String?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isSyncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(10.dp),
                color = SlhTextMuted,
                strokeWidth = 1.5.dp,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = when {
                isSyncing -> "Classroom: обновляем…"
                lastSyncAt != null -> "Classroom обновлён ${
                    java.time.Instant.ofEpochMilli(lastSyncAt)
                        .atZone(java.time.ZoneId.systemDefault())
                        .format(syncFormatter)
                }"
                else -> "Classroom ещё не синхронизирован"
            },
            color = SlhTextMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = errorMessage,
            color = SlhDanger,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun WeekStrip(
    state: TodayUiState,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .padding(horizontal = 6.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArrowButton(text = "‹", onClick = onPreviousWeek)
            Text(
                text = weekTitle(state),
                color = SlhTextSecondary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            ArrowButton(text = "›", onClick = onNextWeek)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            state.week.forEach { day ->
                DayCell(
                    day = day,
                    isToday = day.date == state.today,
                    isSelected = day.date == state.selectedDay,
                    isPast = day.date.isBefore(state.today),
                    onClick = { onDayClick(day.date) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    day: WeekDay,
    isToday: Boolean,
    isSelected: Boolean,
    isPast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    val background = when {
        isSelected -> SlhAccent
        isToday -> SlhOutline
        else -> SlhSurface
    }
    val numberColor = when {
        isSelected -> SlhOnAccent
        isPast -> SlhTextMuted
        else -> SlhContent
    }
    Column(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(shape)
            .background(background)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, ruLocale).uppercase(ruLocale),
            color = if (isSelected) SlhOnAccent else SlhTextMuted,
            fontFamily = SlhSans,
            fontSize = 10.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = day.date.dayOfMonth.toString(),
            color = numberColor,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.height(5.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            repeat(day.count.coerceAtMost(3)) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(SlhDotShape)
                        .background(
                            when {
                                isSelected -> SlhOnAccent
                                day.hasRedo -> SlhDanger
                                else -> SlhAccent
                            },
                        ),
                )
            }
        }
    }
}

@Composable
private fun ArrowButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(50))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun AgendaRow(
    item: AgendaItem,
    now: Long,
    onClick: () -> Unit,
) {
    val due = item.dueAt
    val isOverdue = due != null && due < now
    val isSoon = due != null && daysUntil(due, now) <= 2L
    val highlight = item.isRedo || isOverdue || isSoon
    val shape = RoundedCornerShape(8.dp)
    val label = if (item.isRedo) "переделать" else relativeDueLabel(due, now)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, if (highlight) SlhAccent else SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SourceMark(source = item.source)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = SlhContent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subjectTitle,
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = when {
                item.isRedo || isOverdue -> SlhDanger
                highlight -> SlhAccent
                else -> SlhTextSecondary
            },
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun SourceMark(source: AgendaSource) {
    Box(
        modifier = Modifier
            .size(width = 26.dp, height = 18.dp)
            .clip(RoundedCornerShape(3.dp))
            .border(1.dp, SlhOutline, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (source == AgendaSource.CLASSROOM) "GC" else "ЛР",
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
        )
    }
}

@Composable
private fun SummaryCard(
    average: Double?,
    total: Int,
    done: Int,
    defended: Int,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Средний балл по практическим",
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "сделано $done/$total · защищено $defended/$total",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SlhProgressDots(total = total.coerceAtMost(24), filled = scaled(done, total, 24))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "подробная статистика ›",
                color = SlhAccent,
                fontFamily = SlhMono,
                fontSize = 11.sp,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        if (average != null) {
            DotMatrixText(
                text = String.format(Locale.US, "%.1f", average),
                color = SlhAccent,
                dotSize = 4.5.dp,
                dotGap = 1.dp,
            )
        } else {
            Text(
                text = "—",
                color = SlhTextDisabled,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
        }
    }
}

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
            text = "С чего начать",
            color = SlhContent,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "1. Загрузи курсы из Google Classroom — задания и дедлайны подтянутся сами.\n" +
                "2. Во вкладке «Предметы» добавь курсы как предметы и отмечай лабы и практики.\n" +
                "3. Здесь будет всё, что пора сдавать.",
            color = SlhTextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(18.dp))
        PrimaryAction(
            text = if (isSyncing) "ЗАГРУЖАЕМ…" else "ЗАГРУЗИТЬ ИЗ CLASSROOM",
            enabled = !isSyncing,
            onClick = onSyncClick,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextAction(text = "или добавить предметы вручную ›", onClick = onOpenSubjects)
    }
}

@Composable
private fun PrimaryAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(shape)
            .background(if (enabled) SlhAccent else SlhOutline)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) SlhOnAccent else SlhTextMuted,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun TextAction(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun EmptyLine(text: String) {
    Text(
        text = text,
        color = SlhTextMuted,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 10.dp),
    )
}

private fun weekTitle(state: TodayUiState): String = when (state.weekOffset) {
    0 -> "эта неделя"
    1 -> "следующая неделя"
    -1 -> "прошлая неделя"
    else -> state.week.firstOrNull()?.date?.format(monthFormatter) ?: ""
}

private fun scaled(value: Int, total: Int, max: Int): Int =
    if (total <= max) value else (value.toDouble() / total * max).toInt()

private fun coursesWord(count: Int): String {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> "курсов"
        mod10 == 1 -> "курс"
        mod10 in 2..4 -> "курса"
        else -> "курсов"
    }
}

private const val PREVIEW_NOW = 1_792_700_000_000L

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 900)
@Composable
private fun TodayScreenPreview() {
    val today = LocalDate.of(2026, 10, 22)
    StudentLabHubTheme {
        TodayScreen(
            state = TodayUiState(
                isLoading = false,
                now = PREVIEW_NOW,
                today = today,
                week = (0L..6L).map { shift ->
                    WeekDay(date = today.minusDays(3).plusDays(shift), count = (shift % 3).toInt(), hasRedo = shift == 4L)
                },
                redo = listOf(
                    AgendaItem("r1", "ЛР №3 · Функц. требования", "ТРПО", PREVIEW_NOW - 86_400_000, true, AgendaSource.CLASSROOM),
                ),
                upcoming = listOf(
                    AgendaItem("u1", "ЛР №5 · Структурное пр.", "ТРПО", PREVIEW_NOW + 86_400_000, false, AgendaSource.CLASSROOM),
                    AgendaItem("u2", "ПР №3 · Тест-кейсы", "ТРПО", PREVIEW_NOW + 4 * 86_400_000, false, AgendaSource.LAB),
                ),
                overallAverage = 8.3,
                totalWorks = 20,
                doneWorks = 15,
                defendedWorks = 12,
                subjectsCount = 3,
                coursesCount = 3,
                unlinkedCourses = 1,
                lastSyncAt = PREVIEW_NOW,
            ),
            onSyncClick = {},
            onPreviousWeek = {},
            onNextWeek = {},
            onDayClick = {},
            onItemClick = {},
            onOpenSubjects = {},
            onOpenStatistics = {},
        )
    }
}
