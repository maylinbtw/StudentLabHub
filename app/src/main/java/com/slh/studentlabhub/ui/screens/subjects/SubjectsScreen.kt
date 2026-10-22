package com.slh.studentlabhub.ui.screens.subjects

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.components.SlhProgressDots
import com.slh.studentlabhub.ui.components.relativeDueLabel
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

@Composable
fun SubjectsRoute(
    onSubjectClick: (subjectId: Long) -> Unit,
    onCourseClick: (courseId: String) -> Unit,
    onOpenArchive: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubjectsViewModel = viewModel(factory = SubjectsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    SubjectsScreen(
        state = state,
        onAddSubjectClick = { showAddDialog = true },
        onSubjectClick = onSubjectClick,
        onCourseClick = onCourseClick,
        onAddCourse = viewModel::addCourse,
        onAddAllCourses = viewModel::addAllCourses,
        onArchiveCourse = viewModel::archiveCourse,
        onArchiveSemester = viewModel::archiveSemester,
        onOpenArchive = onOpenArchive,
        modifier = modifier,
    )

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, semester, labsCount, practiceCount ->
                viewModel.addSubject(title, semester, labsCount, practiceCount)
                showAddDialog = false
            },
        )
    }
}

@Composable
fun SubjectsScreen(
    state: SubjectsUiState,
    onAddSubjectClick: () -> Unit,
    onSubjectClick: (subjectId: Long) -> Unit,
    onCourseClick: (courseId: String) -> Unit,
    onAddCourse: (CourseOverview) -> Unit,
    onAddAllCourses: () -> Unit,
    modifier: Modifier = Modifier,
    onArchiveCourse: (CourseOverview) -> Unit = {},
    onArchiveSemester: (Int) -> Unit = {},
    onOpenArchive: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = "Лабы, практики и Classroom",
            title = "ПРЕДМЕТЫ",
            actionIcon = SlhIcon.Plus,
            onActionClick = onAddSubjectClick,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
        ) {
            if (state.isEmpty) {
                item(key = "empty") {
                    EmptyCard(onAddSubjectClick = onAddSubjectClick)
                }
                if (state.archivedCount > 0) {
                    item(key = "archive") {
                        Spacer(modifier = Modifier.height(16.dp))
                        ArchiveLink(count = state.archivedCount, onClick = onOpenArchive)
                    }
                }
                return@LazyColumn
            }

            if (state.unlinkedCourses.isNotEmpty()) {
                item(key = "import_label") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionLabel(
                            text = "ИЗ CLASSROOM · ЕЩЁ НЕ ДОБАВЛЕНЫ",
                            modifier = Modifier.weight(1f),
                        )
                        if (state.unlinkedCourses.size > 1) {
                            Text(
                                text = "добавить все",
                                color = SlhAccent,
                                fontFamily = SlhMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(role = Role.Button, onClick = onAddAllCourses)
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(items = state.unlinkedCourses, key = { "course_${it.id}" }) { course ->
                    ImportCourseRow(
                        course = course,
                        onClick = { onCourseClick(course.id) },
                        onAdd = { onAddCourse(course) },
                        onArchive = { onArchiveCourse(course) },
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                item(key = "import_hint") {
                    Text(
                        text = "«Добавить» создаёт предмет, связанный с курсом. × — убрать курс в архив, если он уже не ведётся.",
                        color = SlhTextMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            state.groups.forEach { group ->
                item(key = "semester_${group.semester}") {
                    SemesterHeader(
                        semester = group.semester,
                        onArchive = { onArchiveSemester(group.semester) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(items = group.subjects, key = { "subject_${it.summary.id}" }) { item ->
                    SubjectCard(
                        item = item,
                        now = state.now,
                        onClick = { onSubjectClick(item.summary.id) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SlhProgressDots(total = item.summary.totalLabs, filled = item.summary.doneLabs)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            item(key = "add_subject") {
                OutlineAction(text = "добавить предмет вручную", onClick = onAddSubjectClick)
            }
            if (state.archivedCount > 0) {
                item(key = "archive") {
                    Spacer(modifier = Modifier.height(16.dp))
                    ArchiveLink(count = state.archivedCount, onClick = onOpenArchive)
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(
    item: SubjectItem,
    now: Long,
    onClick: () -> Unit,
) {
    val subject = item.summary
    val course = item.course
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subject.title,
                    color = SlhWhite,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (subject.classroomCourseId != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(text = "GC")
                }
            }
            course?.teacherName?.let { teacher ->
                Spacer(modifier = Modifier.height(3.dp))
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
                text = worksLine(subject),
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (course != null) {
                Spacer(modifier = Modifier.height(3.dp))
                val active = course.tasksCount - course.completedCount - course.redoCount
                Text(
                    text = buildString {
                        append("Classroom: ")
                        append(if (active > 0) "$active активн." else "всё сдано")
                        course.nearestDeadline?.let { append(" · ближ. ${relativeDueLabel(it, now)}") }
                    },
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (course.redoCount > 0) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${course.redoCount} на доработку",
                        color = SlhDanger,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        val average = subject.averageScore ?: course?.averageScore
        if (average != null) {
            DotMatrixText(
                text = String.format(Locale.US, "%.1f", average),
                color = SlhAccent,
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

@Composable
private fun ImportCourseRow(
    course: CourseOverview,
    onClick: () -> Unit,
    onAdd: () -> Unit,
    onArchive: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = course.title,
                color = SlhWhite,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = listOfNotNull(course.teacherName, "${course.tasksCount} ${tasksWord(course.tasksCount)}").joinToString(" · "),
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .height(30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SlhAccent)
                .clickable(role = Role.Button, onClick = onAdd)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+ ДОБАВИТЬ",
                color = SlhOnAccent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(role = Role.Button, onClick = onArchive)
                .semantics { contentDescription = "Убрать курс в архив" },
            contentAlignment = Alignment.Center,
        ) {
            SlhIconView(icon = SlhIcon.Close, color = SlhTextMuted, size = 11.dp)
        }
    }
}

@Composable
private fun SemesterHeader(
    semester: Int,
    onArchive: () -> Unit,
) {
    var confirm by remember(semester) { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionLabel(
            text = "СЕМЕСТР $semester",
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (confirm) "весь семестр в архив?" else "в архив",
            color = if (confirm) SlhDanger else SlhTextMuted,
            fontFamily = SlhMono,
            fontSize = 11.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(role = Role.Button) {
                    if (confirm) {
                        confirm = false
                        onArchive()
                    } else {
                        confirm = true
                    }
                }
                .padding(horizontal = 6.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun ArchiveLink(
    count: Int,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Архив",
            color = SlhTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$count ›",
            color = SlhTextMuted,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun Badge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .border(1.dp, SlhOutline, RoundedCornerShape(3.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp),
    ) {
        Text(
            text = text,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
        )
    }
}

@Composable
private fun EmptyCard(onAddSubjectClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Пока нет предметов",
            color = SlhWhite,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Курсы из Google Classroom появятся здесь после синхронизации на вкладке «Сегодня». " +
                "Или добавь предмет вручную — с нужным числом лабораторных и практических.",
            color = SlhTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlineAction(text = "добавить предмет", onClick = onAddSubjectClick)
    }
}

@Composable
private fun OutlineAction(
    text: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlhIconView(icon = SlhIcon.Plus, color = SlhTextMuted, size = 12.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
        )
    }
}

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

private fun worksLine(subject: SubjectSummary): String =
    if (subject.totalLabs == 0) {
        "работ пока нет — добавь внутри предмета"
    } else {
        "${subject.doneLabs}/${subject.totalLabs} сделано · ${subject.defendedLabs}/${subject.totalLabs} защищено"
    }

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 800)
@Composable
private fun SubjectsScreenPreview() {
    StudentLabHubTheme {
        SubjectsScreen(
            state = SubjectsUiState(
                isLoading = false,
                groups = listOf(
                    SemesterGroup(
                        semester = 4,
                        subjects = listOf(
                            SubjectItem(
                                summary = SubjectSummary(1, "ТРПО", 4, 9, 7, 5, 8.5, "c1"),
                                course = CourseOverview("c1", "ТРПО", "Иванова А. С.", 8, 5, 1, 1_792_800_000_000, 8.3),
                            ),
                            SubjectItem(
                                summary = SubjectSummary(2, "Операционные системы", 4, 6, 5, 4, 7.5),
                                course = null,
                            ),
                        ),
                    ),
                ),
                unlinkedCourses = listOf(
                    CourseOverview("c2", "Криптография", "Петров И. Н.", 6, 2, 0, null, null),
                ),
                now = 1_792_700_000_000,
            ),
            onAddSubjectClick = {},
            onSubjectClick = {},
            onCourseClick = {},
            onAddCourse = {},
            onAddAllCourses = {},
        )
    }
}
