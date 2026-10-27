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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.LinkBanner
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.components.SlhSwipeRow
import com.slh.studentlabhub.ui.components.completedLabel
import com.slh.studentlabhub.ui.components.addButtonLabel
import com.slh.studentlabhub.ui.components.displayName
import com.slh.studentlabhub.ui.components.relativeDueLabel
import com.slh.studentlabhub.ui.components.sectionTitle
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhOnContent
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.components.NextLessonCard

@Composable
fun LabListRoute(
    onBack: () -> Unit,
    onLabClick: (Lab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LabListViewModel = viewModel(factory = LabListViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

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
        onOpenClassroom = { link -> runCatching { uriHandler.openUri(link) } },
        onArchive = { viewModel.setArchived(true) },
        onSetStatus = viewModel::setLabStatus,
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
    onUpdateSubject: (title: String, semester: Int, scheduleTitle: String?) -> Unit,
    onDeleteSubject: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenClassroom: (String) -> Unit = {},
    onArchive: () -> Unit = {},
    onRestore: () -> Unit = {},
    onSetStatus: (Lab, LabStatus) -> Unit = { _, _ -> },
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
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
                isArchived -> Strings.get(R.string.subject_archived_overline)
                linkedCourseId != null -> Strings.get(R.string.subject_classroom_overline)
                else -> Strings.get(R.string.subject_overline)
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
                                text = Strings.get(R.string.edit_subject),
                                color = SlhContent,
                                fontFamily = SlhSans,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            showEditDialog = true
                        },
                    )
                    state.course?.link?.let { link ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = Strings.get(R.string.open_course_in_classroom),
                                    color = SlhContent,
                                    fontFamily = SlhSans,
                                    fontSize = 14.sp,
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenClassroom(link)
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isArchived) Strings.get(R.string.restore_from_archive) else Strings.get(R.string.move_to_archive),
                                color = SlhContent,
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
                                text = Strings.get(R.string.delete_subject),
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
                                overline = Strings.get(R.string.archived_banner_overline),
                                title = Strings.get(R.string.archived_banner_title),
                                subtitle = Strings.get(R.string.archived_banner_subtitle),
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
                    if (!isArchived && (state.scheduleConfigured || state.nextLesson != null)) {
                        item(key = "next_lesson") {
                            Spacer(modifier = Modifier.height(14.dp))
                            NextLessonCard(
                                lesson = state.nextLesson,
                                queued = state.queuedForNext,
                                isConfigured = state.scheduleConfigured,
                            )
                        }
                    }
                    state.course?.teacherName?.let { teacher ->
                        item(key = "teacher") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = Strings.get(R.string.teacher_name, teacher),
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    if (details.labs.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = Strings.get(R.string.subject_no_works),
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
                                val next = lab.status.next()
                                val previous = lab.status.previous()
                                SlhSwipeRow(
                                    forwardLabel = next?.let { statusLabel(it, lab.type) },
                                    backwardLabel = previous?.let { statusLabel(it, lab.type) },
                                    onForward = { next?.let { onSetStatus(lab, it) } },
                                    onBackward = { previous?.let { onSetStatus(lab, it) } },
                                ) {
                                    LabRow(
                                        lab = lab,
                                        onClick = { onLabClick(lab) },
                                    )
                                }
                                HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                            }
                        }
                    }
                    if (details.labs.isNotEmpty()) {
                        item(key = "swipe_hint") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = Strings.get(R.string.swipe_hint),
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
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
            initialScheduleTitle = details.summary.scheduleTitle,
            scheduleSubjects = state.scheduleSubjects,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, semester, scheduleTitle ->
                onUpdateSubject(title, semester, scheduleTitle)
                showEditDialog = false
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SlhSurface,
            title = {
                Text(
                    text = Strings.get(R.string.delete_subject_q),
                    color = SlhContent,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = Strings.get(R.string.delete_subject_text, details?.summary?.title.orEmpty()),
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
                        text = Strings.get(R.string.action_delete),
                        color = SlhDanger,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(
                        text = Strings.get(R.string.action_cancel),
                        color = SlhTextMuted,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
        )
    }
}


@Composable
private fun StatsRow(summary: SubjectSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatColumn(
            value = summary.averageScore?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
            label = Strings.get(R.string.stat_avg),
            valueColor = SlhAccent,
        )
        StatColumn(
            value = "${summary.doneLabs}/${summary.totalLabs}",
            label = Strings.get(R.string.stat_done),
            valueColor = SlhContent,
        )
        StatColumn(
            value = "${summary.defendedLabs}/${summary.totalLabs}",
            label = Strings.get(R.string.stat_defended),
            valueColor = SlhContent,
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

private fun LabStatus.next(): LabStatus? = when (this) {
    LabStatus.NOT_DONE -> LabStatus.DONE
    LabStatus.DONE -> LabStatus.DEFENDED
    LabStatus.DEFENDED -> null
}

private fun LabStatus.previous(): LabStatus? = when (this) {
    LabStatus.NOT_DONE -> null
    LabStatus.DONE -> LabStatus.NOT_DONE
    LabStatus.DEFENDED -> LabStatus.DONE
}

private fun statusLabel(status: LabStatus, type: WorkType): String = when (status) {
    LabStatus.NOT_DONE -> Strings.get(R.string.status_not_done)
    LabStatus.DONE -> Strings.get(R.string.status_done)
    LabStatus.DEFENDED -> type.completedLabel.lowercase()
}

@Composable
private fun LabRow(
    lab: Lab,
    onClick: () -> Unit,
) {
    val isActive = lab.status != LabStatus.NOT_DONE
    val showScore = lab.score != null && (isActive || lab.needsRedo)
    val now = System.currentTimeMillis()
    val dueText = if (!isActive && !lab.needsRedo) lab.deadline?.let { relativeDueLabel(it, now) } else null
    val isLate = !isActive && lab.deadline != null && lab.deadline < now
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusBox(status = lab.status)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lab.displayName,
                color = if (isActive) SlhContent else SlhTextSecondary,
                fontFamily = SlhSans,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            when {
                lab.defendQueued && lab.status != LabStatus.DEFENDED -> Text(
                    text = Strings.get(R.string.defense_queued_short),
                    color = SlhAccent,
                    style = MaterialTheme.typography.bodySmall,
                )
                lab.needsRedo -> Text(
                    text = Strings.get(R.string.needs_redo),
                    color = SlhDanger,
                    style = MaterialTheme.typography.bodySmall,
                )
                dueText != null -> Text(
                    text = Strings.get(R.string.due_label, dueText),
                    color = if (isLate) SlhDanger else SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
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
                modifier = baseModifier.background(SlhContent),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "✓", color = SlhOnContent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
            color = if (completed) SlhOnAccent else SlhContent,
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
            onUpdateSubject = { _, _, _ -> },
            onDeleteSubject = {},
        )
    }
}
