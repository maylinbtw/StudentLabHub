package com.slh.studentlabhub.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.R
import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.components.LessonRow
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhPullRefresh
import com.slh.studentlabhub.ui.components.dayLabel
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ScheduleRoute(
    onBack: () -> Unit,
    onOpenSubject: (subjectId: Long) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ScheduleScreen(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onOpenSubject = onOpenSubject,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}

@Composable
fun ScheduleScreen(
    state: ScheduleUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOpenSubject: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = state.title.ifBlank { Strings.get(R.string.schedule_banner_overline) },
            title = Strings.get(R.string.schedule_title),
            onBackClick = onBack,
            actionIcon = SlhIcon.Gear,
            onActionClick = onOpenSettings,
        )
        SlhPullRefresh(
            isRefreshing = state.isSyncing,
            onRefresh = onRefresh,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                val status = state.errorMessage ?: state.syncAt?.let {
                    Strings.get(R.string.schedule_updated_at, formatSync(it))
                }
                status?.let { text ->
                    item(key = "status") {
                        Text(
                            text = text,
                            color = if (state.errorMessage != null) SlhDanger else SlhTextMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                if (!state.isLoading && state.days.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = if (state.isConfigured) {
                                Strings.get(R.string.schedule_no_lessons)
                            } else {
                                Strings.get(R.string.next_lesson_not_configured)
                            },
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                        )
                    }
                }
                state.days.forEach { day ->
                    item(key = "day_${day.date}") {
                        SectionLabel(
                            text = Strings.get(
                                R.string.schedule_day_section,
                                day.date.dayLabel(today).uppercase(Locale.getDefault()),
                                day.lessons.size,
                            ),
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    items(items = day.lessons, key = { "lesson_${it.id}" }) { lesson ->
                        val subjectId = state.lessonSubjectIds[lesson.id]
                        LessonRow(
                            lesson = lesson,
                            isNow = lesson.id == state.currentLessonId,
                            queued = state.defense[lesson.id].orEmpty(),
                            onClick = subjectId?.let { id -> { onOpenSubject(id) } },
                        )
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    item(key = "space_${day.date}") {
                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
            }
        }
    }
}

private fun formatSync(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd.MM HH:mm", Locale.getDefault()))

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 760)
@Composable
private fun ScheduleScreenPreview() {
    StudentLabHubTheme {
        val today = LocalDate.now()
        ScheduleScreen(
            state = ScheduleUiState(
                isLoading = false,
                isConfigured = true,
                title = "ПЗТ-44 · 2 подгруппа",
                days = listOf(
                    ScheduleDay(
                        today,
                        listOf(
                            Lesson(1, today, 1, LocalTime.of(9, 0), LocalTime.of(10, 40), "Иностранный язык", "Циунчик Л.И.", "106"),
                            Lesson(2, today, 3, LocalTime.of(13, 10), LocalTime.of(14, 20), "Тестирование программного обеспечения", "Орехво В.Д.", "304"),
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRefresh = {},
            onOpenSubject = {},
            onOpenSettings = {},
        )
    }
}
