package com.slh.studentlabhub.ui.screens.lablist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.slh.studentlabhub.domain.model.ClassroomTask
import com.slh.studentlabhub.domain.model.CourseTasks
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.TaskState
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.LinkBanner
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.components.addButtonLabel
import com.slh.studentlabhub.ui.components.displayName
import com.slh.studentlabhub.ui.components.relativeDueLabel
import com.slh.studentlabhub.ui.components.sectionTitle
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhInk
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

@Composable
fun LabListRoute(
    onBack: () -> Unit,
    onLabClick: (Lab) -> Unit,
    onOpenCourse: (courseId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LabListViewModel = viewModel(factory = LabListViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isMissing) {
        if (state.isMissing) {
            onBack()
        }
    }

    LabListScreen(
        state = state,
        onBack = onBack,
        onLabClick = onLabClick,
        onAddWork = viewModel::addWork,
        onUpdateSubject = viewModel::updateSubject,
        onDeleteSubject = viewModel::deleteSubject,
        onOpenCourse = onOpenCourse,
        onLinkCourse = viewModel::linkCourse,
        onUnlinkCourse = viewModel::unlinkCourse,
        onArchive = { viewModel.setArchived(true) },
        onRestore = { viewModel.setArchived(false) },
        modifier = modifier,
    )
}

@Composable
fun LabListScreen(
    state: LabListUiState,
    onBack: () -> Unit,
    onLabClick: (Lab) -> Unit,
    onAddWork: (WorkType) -> Unit,
    onUpdateSubject: (title: String, semester: Int) -> Unit,
    onDeleteSubject: () -> Unit,
    onOpenCourse: (courseId: String) -> Unit,
    modifier: Modifier = Modifier,
    onLinkCourse: (courseId: String) -> Unit = {},
    onUnlinkCourse: () -> Unit = {},
    onArchive: () -> Unit = {},
    onRestore: () -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showCoursePicker by rememberSaveable { mutableStateOf(false) }
    val details = state.details
    val linkedCourseId = details?.summary?.classroomCourseId
    val isArchived = details?.summary?.archived == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = when {
                isArchived -> "Предмет · в архиве"
                linkedCourseId != null -> "Предмет · связан с Classroom"
                else -> "Предмет"
            },
            title = details?.summary?.title?.uppercase(Locale.getDefault()) ?: "",
            onBackClick = onBack,
            actionIcon = SlhIcon.More,
            onActionClick = { menuExpanded = true },
            actionMenu = {
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = SlhSurface,
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Редактировать предмет",
                                color = SlhWhite,
                                fontFamily = SlhSans,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            showEditDialog = true
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (linkedCourseId == null) "Связать с курсом Classroom" else "Сменить курс Classroom",
                                color = SlhWhite,
                                fontFamily = SlhSans,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            showCoursePicker = true
                        },
                    )
                    if (linkedCourseId != null) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Отвязать курс Classroom",
                                    color = SlhWhite,
                                    fontFamily = SlhSans,
                                    fontSize = 14.sp,
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onUnlinkCourse()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isArchived) "Вернуть из архива" else "В архив",
                                color = SlhWhite,
                                fontFamily = SlhSans,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            if (isArchived) onRestore() else onArchive()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Удалить предмет",
                                color = SlhDanger,
                                fontFamily = SlhSans,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            showDeleteConfirm = true
                        },
                    )
                }
            },
        )

        when {
            state.isLoading || details == null -> {
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
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                ) {
                    if (isArchived) {
                        item(key = "archived_banner") {
                            LinkBanner(
                                overline = "В АРХИВЕ",
                                title = "Вернуть предмет в список",
                                subtitle = "Сейчас он скрыт из «Сегодня» и «Предметов»",
                                onClick = onRestore,
                                accent = false,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    item(key = "stats") {
                        StatsRow(summary = details.summary)
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    item(key = "classroom") {
                        Spacer(modifier = Modifier.height(14.dp))
                        ClassroomSection(
                            linkedCourseId = linkedCourseId,
                            course = state.course,
                            hasAvailableCourses = state.availableCourses.isNotEmpty(),
                            now = state.now,
                            onOpenCourse = onOpenCourse,
                            onLinkClick = { showCoursePicker = true },
                        )
                    }
                    if (details.labs.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = "В этом предмете пока нет работ",
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                            )
                        }
                    }
                    WorkType.entries.forEach { type ->
                        val works = details.labs.filter { it.type == type }
                        if (works.isNotEmpty()) {
                            item(key = "section_${type.name}") {
                                Spacer(modifier = Modifier.height(18.dp))
                                SectionLabel(text = "${type.sectionTitle} · ${works.size}")
                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                            }
                            items(items = works, key = { it.id }) { lab ->
                                LabRow(
                                    lab = lab,
                                    onClick = { onLabClick(lab) },
                                )
                                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                            }
                        }
                    }
                    item(key = "add_buttons") {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            WorkType.entries.forEach { type ->
                                AddWorkButton(
                                    text = type.addButtonLabel,
                                    onClick = { onAddWork(type) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && details != null) {
        EditSubjectDialog(
            initialTitle = details.summary.title,
            initialSemester = details.summary.semester,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, semester ->
                onUpdateSubject(title, semester)
                showEditDialog = false
            },
        )
    }

    if (showCoursePicker && details != null) {
        CoursePickerDialog(
            subjectTitle = details.summary.title,
            courses = state.availableCourses,
            currentCourseId = linkedCourseId,
            onDismiss = { showCoursePicker = false },
            onPick = { courseId ->
                onLinkCourse(courseId)
                showCoursePicker = false
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SlhSurface,
            title = {
                Text(
                    text = "Удалить предмет?",
                    color = SlhWhite,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = "Предмет «${details?.summary?.title.orEmpty()}» и все его работы будут удалены без возможности восстановления.",
                    color = SlhTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteSubject()
                    },
                ) {
                    Text(
                        text = "УДАЛИТЬ",
                        color = SlhDanger,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(
                        text = "ОТМЕНА",
                        color = SlhTextMuted,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
        )
    }
}

@Composable
private fun ClassroomSection(
    linkedCourseId: String?,
    course: CourseTasks?,
    hasAvailableCourses: Boolean,
    now: Long,
    onOpenCourse: (courseId: String) -> Unit,
    onLinkClick: () -> Unit,
) {
    when {
        linkedCourseId == null -> {
            if (hasAvailableCourses) {
                LinkBanner(
                    overline = "GOOGLE CLASSROOM",
                    title = "Связать с курсом",
                    subtitle = "Задания и дедлайны курса появятся прямо здесь",
                    onClick = onLinkClick,
                    accent = false,
                )
            }
        }

        course == null -> {
            Text(
                text = "Курс Classroom не найден. Обнови данные на вкладке «Сегодня» или свяжи предмет заново через ⋮.",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        else -> {
            val active = course.tasks
                .filter { it.state != TaskState.COMPLETED }
                .sortedWith(compareBy<ClassroomTask>({ it.state != TaskState.REDO }, { it.dueAt ?: Long.MAX_VALUE }))
            val completed = course.tasks.count { it.state == TaskState.COMPLETED }
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel(
                    text = "CLASSROOM · СДАНО $completed/${course.tasks.size}",
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "все задания ›",
                    color = SlhAccent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(role = Role.Button, onClick = { onOpenCourse(course.id) })
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                )
            }
            course.teacherName?.let { teacher ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = teacher,
                    color = SlhTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (active.isEmpty()) {
                Text(
                    text = "Все задания курса сданы",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            active.take(CLASSROOM_PREVIEW_COUNT).forEach { task ->
                ClassroomTaskRow(
                    task = task,
                    now = now,
                    onClick = { onOpenCourse(course.id) },
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (active.size > CLASSROOM_PREVIEW_COUNT) {
                Text(
                    text = "и ещё ${active.size - CLASSROOM_PREVIEW_COUNT} — в «все задания»",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ClassroomTaskRow(
    task: ClassroomTask,
    now: Long,
    onClick: () -> Unit,
) {
    val isRedo = task.state == TaskState.REDO
    val due = task.dueAt
    val isOverdue = due != null && due < now
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, if (isRedo || isOverdue) SlhAccent else SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = task.title,
            color = SlhWhite,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = if (isRedo) "переделать" else relativeDueLabel(due, now),
            color = if (isRedo || isOverdue) SlhDanger else SlhTextSecondary,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
    }
}

private const val CLASSROOM_PREVIEW_COUNT = 3

@Composable
private fun StatsRow(summary: SubjectSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatColumn(
            value = summary.averageScore?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
            label = "СР. БАЛЛ",
            valueColor = SlhAccent,
        )
        StatColumn(
            value = "${summary.doneLabs}/${summary.totalLabs}",
            label = "СДЕЛАНО",
            valueColor = SlhWhite,
        )
        StatColumn(
            value = "${summary.defendedLabs}/${summary.totalLabs}",
            label = "ЗАЩИЩЕНО",
            valueColor = SlhWhite,
        )
    }
}

@Composable
private fun StatColumn(
    value: String,
    label: String,
    valueColor: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DotMatrixText(
            text = value,
            color = valueColor,
            dotSize = 3.5.dp,
            dotGap = 1.dp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
        )
    }
}

@Composable
private fun LabRow(
    lab: Lab,
    onClick: () -> Unit,
) {
    val isActive = lab.status != LabStatus.NOT_DONE
    val showScore = lab.type == WorkType.PRACTICE && lab.score != null && isActive
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusBox(status = lab.status)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = lab.displayName,
            color = if (isActive) SlhWhite else SlhTextMuted,
            fontFamily = SlhSans,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (showScore && lab.score != null) {
            Spacer(modifier = Modifier.width(12.dp))
            ScoreChip(score = lab.score, completed = lab.status == LabStatus.DEFENDED)
        }
    }
}

@Composable
private fun StatusBox(status: LabStatus) {
    val shape = RoundedCornerShape(3.dp)
    val baseModifier = Modifier
        .size(20.dp)
        .clip(shape)
    when (status) {
        LabStatus.DEFENDED -> {
            Box(
                modifier = baseModifier.background(SlhAccent),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "★", color = SlhOnAccent, fontSize = 12.sp)
            }
        }

        LabStatus.DONE -> {
            Box(
                modifier = baseModifier.background(SlhWhite),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "✓", color = SlhInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        LabStatus.NOT_DONE -> {
            Box(
                modifier = baseModifier.border(1.5.dp, SlhOutline, shape),
            )
        }
    }
}

@Composable
private fun ScoreChip(
    score: Int,
    completed: Boolean,
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 30.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (completed) SlhAccent else SlhDotInactive)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = score.toString(),
            color = if (completed) SlhOnAccent else SlhWhite,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun AddWorkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .height(38.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlhIconView(icon = SlhIcon.Plus, color = SlhTextMuted, size = 12.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
            maxLines = 1,
        )
    }
}

private val previewDetails = SubjectDetails(
    summary = SubjectSummary(
        id = 1,
        title = "ТРПО",
        semester = 4,
        totalLabs = 9,
        doneLabs = 7,
        defendedLabs = 5,
        averageScore = 8.5,
    ),
    labs = listOf(
        Lab(1, 1, WorkType.LAB, 1, "Анализ задачи", LabStatus.DEFENDED, null, "", null),
        Lab(2, 1, WorkType.LAB, 2, "Требования", LabStatus.DEFENDED, null, "", null),
        Lab(3, 1, WorkType.LAB, 3, "Функц. требования", LabStatus.DEFENDED, null, "", null),
        Lab(4, 1, WorkType.LAB, 4, "Модель ЖЦ", LabStatus.DONE, null, "", null),
        Lab(5, 1, WorkType.LAB, 5, "Структурное пр.", LabStatus.DONE, null, "", null),
        Lab(6, 1, WorkType.LAB, 6, "UX-прототипы", LabStatus.NOT_DONE, null, "", null),
        Lab(7, 1, WorkType.PRACTICE, 1, "Диаграмма вариантов использования", LabStatus.DEFENDED, 9, "", null),
        Lab(8, 1, WorkType.PRACTICE, 2, "Диаграмма классов", LabStatus.DEFENDED, 8, "", null),
        Lab(9, 1, WorkType.PRACTICE, 3, "Тест-кейсы", LabStatus.NOT_DONE, null, "", null),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 900)
@Composable
private fun LabListScreenPreview() {
    StudentLabHubTheme {
        LabListScreen(
            state = LabListUiState(isLoading = false, details = previewDetails),
            onBack = {},
            onLabClick = {},
            onAddWork = {},
            onUpdateSubject = { _, _ -> },
            onDeleteSubject = {},
            onOpenCourse = {},
        )
    }
}
