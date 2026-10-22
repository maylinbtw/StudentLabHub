package com.slh.studentlabhub.ui.screens.deadlines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.TaskState
import com.slh.studentlabhub.domain.model.UpcomingTask
import com.slh.studentlabhub.ui.auth.rememberGoogleAuthorization
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.components.SlhProgressDots
import com.slh.studentlabhub.ui.theme.SlhBlack
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhRed
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shortDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE, dd.MM", Locale.forLanguageTag("ru"))
private val syncTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

private const val URGENT_WINDOW_MS = 3L * 24 * 60 * 60 * 1000
private const val UPCOMING_LIMIT = 5

@Composable
fun DeadlinesRoute(
    onOpenCourse: (courseId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeadlinesViewModel = viewModel(factory = DeadlinesViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val authorize = rememberGoogleAuthorization(
        onAuthorized = viewModel::sync,
        onError = viewModel::onAuthorizationFailed,
    )

    DeadlinesScreen(
        state = state,
        onOpenCourse = onOpenCourse,
        onSyncClick = {
            if (!state.isSyncing) {
                viewModel.onSyncStarted()
                authorize()
            }
        },
        modifier = modifier,
    )
}

@Composable
fun DeadlinesScreen(
    state: DeadlinesUiState,
    onOpenCourse: (courseId: String) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val highlightedId = state.courses
        .filter { it.nearestDeadline != null }
        .minByOrNull { it.nearestDeadline ?: Long.MAX_VALUE }
        ?.id

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBlack),
    ) {
        ScreenHeader(
            overline = "Google Classroom",
            title = "ДЕДЛАЙНЫ",
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
                    color = SlhRed,
                    strokeWidth = 2.dp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                if (state.upcoming.isNotEmpty()) {
                    item(key = "upcoming_label") {
                        SectionLabel(text = "БЛИЖАЙШИЕ ДЕДЛАЙНЫ")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(
                        items = state.upcoming.take(UPCOMING_LIMIT),
                        key = { "upcoming_${it.task.id}" },
                    ) { upcoming ->
                        UpcomingRow(
                            item = upcoming,
                            now = state.now,
                            onClick = { onOpenCourse(upcoming.courseId) },
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    if (state.upcoming.size > UPCOMING_LIMIT) {
                        item(key = "upcoming_more") {
                            Text(
                                text = "и ещё ${state.upcoming.size - UPCOMING_LIMIT} — в курсах ниже",
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    item(key = "upcoming_space") {
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
                item(key = "header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SectionLabel(
                            text = "МОИ КУРСЫ",
                            modifier = Modifier.weight(1f),
                        )
                        state.lastSyncAt?.let { syncedAt ->
                            Text(
                                text = "обновлено ${syncedAt.format(syncTimeFormatter)}",
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    state.errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            color = SlhRed,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (state.courses.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = if (state.isSyncing) {
                                "Загружаем курсы из Google Classroom…"
                            } else {
                                "Курсы ещё не загружены.\nНажми «синхронизировать с GC»."
                            },
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                        )
                    }
                }
                items(items = state.courses, key = { it.id }) { course ->
                    CourseCard(
                        course = course,
                        highlighted = course.id == highlightedId,
                        onClick = { onOpenCourse(course.id) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SlhProgressDots(total = course.tasksCount, filled = course.completedCount)
                    Spacer(modifier = Modifier.height(14.dp))
                }
                item(key = "sync") {
                    SyncButton(isSyncing = state.isSyncing, onClick = onSyncClick)
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: CourseOverview,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        if (highlighted) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(SlhRed),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, SlhOutline, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    color = SlhWhite,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                course.teacherName?.let { teacher ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = teacher,
                        color = SlhTextSecondary,
                        fontFamily = SlhSans,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = courseSubtitle(course),
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (course.redoCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${course.redoCount} на доработку",
                        color = SlhRed,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            val average = course.averageScore
            if (average != null) {
                DotMatrixText(
                    text = String.format(Locale.US, "%.1f", average),
                    color = SlhRed,
                )
            } else {
                Text(
                    text = "—",
                    color = SlhTextDisabled,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun UpcomingRow(
    item: UpcomingTask,
    now: Long,
    onClick: () -> Unit,
) {
    val due = item.task.dueAt
    val isRedo = item.task.state == TaskState.REDO
    val isOverdue = due != null && due < now
    val isUrgent = isRedo || (due != null && due - now <= URGENT_WINDOW_MS)
    val shape = RoundedCornerShape(8.dp)
    val dueLabel = when {
        isRedo -> "переделать"
        due == null -> "без срока"
        isOverdue -> "просрочено"
        else -> due.format(shortDateFormatter)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, if (isUrgent) SlhRed else SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isUrgent) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SlhRed),
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.task.title,
                color = SlhWhite,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.courseTitle,
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = dueLabel,
            color = if (isUrgent) SlhRed else SlhTextSecondary,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun SyncButton(
    isSyncing: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(enabled = !isSyncing, role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSyncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = SlhTextMuted,
                strokeWidth = 1.5.dp,
            )
        } else {
            SlhIconView(icon = SlhIcon.Refresh, color = SlhTextMuted, size = 14.dp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = if (isSyncing) "синхронизация…" else "синхронизировать с GC",
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
        )
    }
}

private fun courseSubtitle(course: CourseOverview): String {
    val tasks = "${course.tasksCount} ${tasksWord(course.tasksCount)}"
    val deadline = course.nearestDeadline
        ?.let { "дедлайн ${it.format(shortDateFormatter)}" }
        ?: "нет активных дедлайнов"
    return "$tasks · $deadline"
}

private fun Long.format(formatter: DateTimeFormatter): String =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)

private fun tasksWord(count: Int): String {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> "заданий"
        mod10 == 1 -> "задание"
        mod10 in 2..4 -> "задания"
        else -> "заданий"
    }
}

private val previewCourses = listOf(
    CourseOverview(
        id = "1",
        title = "ТРПО",
        teacherName = "Иванова Анна Сергеевна",
        tasksCount = 8,
        completedCount = 5,
        redoCount = 0,
        nearestDeadline = 1_784_000_000_000,
        averageScore = 8.3,
    ),
    CourseOverview(
        id = "2",
        title = "Операционные системы",
        teacherName = "Петров Игорь Николаевич +1",
        tasksCount = 5,
        completedCount = 3,
        redoCount = 0,
        nearestDeadline = 1_785_000_000_000,
        averageScore = 7.5,
    ),
    CourseOverview(
        id = "3",
        title = "Программирование Java",
        teacherName = null,
        tasksCount = 6,
        completedCount = 6,
        redoCount = 0,
        nearestDeadline = null,
        averageScore = null,
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 780)
@Composable
private fun DeadlinesScreenPreview() {
    StudentLabHubTheme {
        DeadlinesScreen(
            state = DeadlinesUiState(
                isLoading = false,
                courses = previewCourses,
                lastSyncAt = 1_784_000_000_000,
            ),
            onOpenCourse = {},
            onSyncClick = {},
        )
    }
}
