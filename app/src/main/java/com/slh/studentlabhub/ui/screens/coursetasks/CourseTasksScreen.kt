package com.slh.studentlabhub.ui.screens.coursetasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.ClassroomTask
import com.slh.studentlabhub.domain.model.CourseTasks
import com.slh.studentlabhub.domain.model.TaskState
import com.slh.studentlabhub.ui.components.LinkBanner
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val URGENT_WINDOW_MS = 3L * 24 * 60 * 60 * 1000

private val dueFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMMM, EEE · HH:mm", Locale.forLanguageTag("ru"))

@Composable
fun CourseTasksRoute(
    onBack: () -> Unit,
    onOpenSubject: (subjectId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseTasksViewModel = viewModel(factory = CourseTasksViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(state.isMissing) {
        if (state.isMissing) {
            onBack()
        }
    }

    CourseTasksScreen(
        state = state,
        onBack = onBack,
        onOpenLink = { link -> runCatching { uriHandler.openUri(link) } },
        onOpenSubject = onOpenSubject,
        onLinkSubject = viewModel::linkSubject,
        onUnlinkSubject = viewModel::unlinkSubject,
        onCreateSubject = viewModel::createLinkedSubject,
        onArchive = { viewModel.setArchived(true) },
        onRestore = { viewModel.setArchived(false) },
        modifier = modifier,
    )
}

@Composable
fun CourseTasksScreen(
    state: CourseTasksUiState,
    onBack: () -> Unit,
    onOpenLink: (String) -> Unit,
    onOpenSubject: (subjectId: Long) -> Unit,
    onLinkSubject: (subjectId: Long) -> Unit,
    onUnlinkSubject: () -> Unit,
    onCreateSubject: () -> Unit,
    modifier: Modifier = Modifier,
    onArchive: () -> Unit = {},
    onRestore: () -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val course = state.course
    val linked = state.linkedSubject

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = if (course?.archived == true) "Задания курса · в архиве" else "Задания курса",
            title = course?.title?.uppercase(Locale.getDefault()) ?: "",
            onBackClick = onBack,
            actionIcon = if (course != null) SlhIcon.More else null,
            onActionClick = { menuExpanded = true },
            actionMenu = {
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = SlhSurface,
                ) {
                    if (course?.link != null) {
                        DropdownMenuItem(
                            text = { MenuText(text = "Открыть в Classroom") },
                            onClick = {
                                menuExpanded = false
                                course.link?.let(onOpenLink)
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            MenuText(text = if (linked == null) "Связать с предметом" else "Сменить предмет")
                        },
                        onClick = {
                            menuExpanded = false
                            showPicker = true
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            MenuText(text = if (course?.archived == true) "Вернуть из архива" else "В архив")
                        },
                        onClick = {
                            menuExpanded = false
                            if (course?.archived == true) onRestore() else onArchive()
                        },
                    )
                    if (linked != null) {
                        DropdownMenuItem(
                            text = { MenuText(text = "Отвязать предмет", color = SlhDanger) },
                            onClick = {
                                menuExpanded = false
                                onUnlinkSubject()
                            },
                        )
                    }
                }
            },
        )

        if (state.isLoading || course == null) {
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
        } else {
            val active = course.tasks
                .filter { it.state != TaskState.COMPLETED }
                .sortedBy { it.dueAt ?: Long.MAX_VALUE }
            val completed = course.tasks
                .filter { it.state == TaskState.COMPLETED }
                .sortedByDescending { it.dueAt ?: Long.MIN_VALUE }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                if (course.archived) {
                    item(key = "archived_banner") {
                        LinkBanner(
                            overline = "В АРХИВЕ",
                            title = "Вернуть курс",
                            subtitle = "Сейчас его задания не показываются на «Сегодня»",
                            onClick = onRestore,
                            accent = false,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                item(key = "subject_link") {
                    if (linked != null) {
                        LinkBanner(
                            overline = "ПРЕДМЕТ · ЛАБЫ И ПРАКТИКИ",
                            title = linked.title,
                            subtitle = "${linked.doneLabs}/${linked.totalLabs} сделано · ${linked.defendedLabs}/${linked.totalLabs} защищено",
                            onClick = { onOpenSubject(linked.id) },
                        )
                    } else {
                        LinkBanner(
                            overline = "НЕ СВЯЗАН С ПРЕДМЕТОМ",
                            title = "Добавить в «Предметы»",
                            subtitle = "Чтобы вести лабы, практики и оценки по этому курсу",
                            onClick = { showPicker = true },
                            accent = false,
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
                item(key = "active_header") {
                    SectionLabel(text = "АКТИВНЫЕ · ${active.size}")
                    Spacer(modifier = Modifier.height(10.dp))
                }
                if (active.isEmpty()) {
                    item(key = "active_empty") {
                        Text(
                            text = "Все задания сданы",
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    }
                }
                items(items = active, key = { "active_${it.id}" }) { task ->
                    ActiveTaskCard(
                        task = task,
                        now = state.now,
                        onClick = { task.link?.let(onOpenLink) },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item(key = "completed_header") {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionLabel(text = "ЗАВЕРШЁННЫЕ · ${completed.size}")
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (completed.isEmpty()) {
                    item(key = "completed_empty") {
                        Text(
                            text = "Пока ничего не сдано",
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    }
                }
                items(items = completed, key = { "done_${it.id}" }) { task ->
                    CompletedTaskRow(
                        task = task,
                        onClick = { task.link?.let(onOpenLink) },
                    )
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                }
            }
        }
    }

    if (showPicker && course != null) {
        SubjectPickerDialog(
            courseTitle = course.title,
            subjects = state.subjects,
            currentSubjectId = linked?.id,
            onDismiss = { showPicker = false },
            onPick = { subjectId ->
                onLinkSubject(subjectId)
                showPicker = false
            },
            onCreate = {
                onCreateSubject()
                showPicker = false
            },
        )
    }
}

@Composable
private fun MenuText(
    text: String,
    color: androidx.compose.ui.graphics.Color = SlhContent,
) {
    Text(
        text = text,
        color = color,
        fontFamily = SlhSans,
        fontSize = 14.sp,
    )
}


@Composable
private fun ActiveTaskCard(
    task: ClassroomTask,
    now: Long,
    onClick: () -> Unit,
) {
    val due = task.dueAt
    val isRedo = task.state == TaskState.REDO
    val isOverdue = due != null && due < now
    val isUrgent = isRedo || (due != null && due - now <= URGENT_WINDOW_MS)
    val shape = RoundedCornerShape(10.dp)

    val dueText = when {
        due == null -> "без срока"
        isOverdue -> "просрочено · ${due.formatDue()}"
        else -> due.formatDue()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        if (isUrgent) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(SlhAccent),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, if (isUrgent) SlhAccent else SlhOutline, shape)
                .clickable(enabled = task.link != null, role = Role.Button, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = SlhContent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dueText,
                    color = if (isUrgent) SlhAccent else SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            TaskChip(
                text = if (isRedo) "переделать" else "!",
                highlighted = isUrgent,
            )
        }
    }
}

@Composable
private fun CompletedTaskRow(
    task: ClassroomTask,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = task.link != null, role = Role.Button, onClick = onClick)
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = task.title,
            color = SlhContent,
            fontFamily = SlhSans,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 36.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SlhDotInactive)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = task.gradeText ?: "★",
                color = SlhAccent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun TaskChip(
    text: String,
    highlighted: Boolean,
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 36.dp)
            .height(26.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (highlighted) SlhAccent else SlhDotInactive)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (highlighted) SlhOnAccent else SlhTextMuted,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

private fun Long.formatDue(): String =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(dueFormatter)

private const val PREVIEW_NOW = 1_784_000_000_000L

private val previewCourse = CourseTasks(
    id = "1",
    title = "ТРПО",
    teacherName = "Иванова Анна Сергеевна",
    link = "https://classroom.google.com",
    tasks = listOf(
        ClassroomTask("1", "ЛР №5 · Структурное пр.", PREVIEW_NOW + 86_400_000, TaskState.ACTIVE, null, "https://classroom.google.com"),
        ClassroomTask("2", "ЛР №6 · UX-прототипы", PREVIEW_NOW + 7 * 86_400_000, TaskState.ACTIVE, null, "https://classroom.google.com"),
        ClassroomTask("3", "ЛР №3 · Функц. требования", PREVIEW_NOW - 5 * 86_400_000, TaskState.REDO, "1/10", "https://classroom.google.com"),
        ClassroomTask("4", "ЛР №4 · Модель ЖЦ", PREVIEW_NOW - 10 * 86_400_000, TaskState.COMPLETED, "9/10", "https://classroom.google.com"),
        ClassroomTask("5", "ЛР №2 · Требования", PREVIEW_NOW - 20 * 86_400_000, TaskState.COMPLETED, null, "https://classroom.google.com"),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 780)
@Composable
private fun CourseTasksScreenPreview() {
    StudentLabHubTheme {
        CourseTasksScreen(
            state = CourseTasksUiState(
                isLoading = false,
                course = previewCourse,
                now = PREVIEW_NOW,
            ),
            onBack = {},
            onOpenLink = {},
            onOpenSubject = {},
            onLinkSubject = {},
            onUnlinkSubject = {},
            onCreateSubject = {},
        )
    }
}
