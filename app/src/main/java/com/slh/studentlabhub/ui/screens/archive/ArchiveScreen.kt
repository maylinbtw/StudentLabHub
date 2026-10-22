package com.slh.studentlabhub.ui.screens.archive

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
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

@Composable
fun ArchiveRoute(
    onBack: () -> Unit,
    onOpenSubject: (subjectId: Long) -> Unit,
    onOpenCourse: (courseId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArchiveViewModel = viewModel(factory = ArchiveViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ArchiveScreen(
        state = state,
        onBack = onBack,
        onOpenSubject = onOpenSubject,
        onOpenCourse = onOpenCourse,
        onRestoreSubject = viewModel::restoreSubject,
        onRestoreCourse = viewModel::restoreCourse,
        modifier = modifier,
    )
}

@Composable
fun ArchiveScreen(
    state: ArchiveUiState,
    onBack: () -> Unit,
    onOpenSubject: (subjectId: Long) -> Unit,
    onOpenCourse: (courseId: String) -> Unit,
    onRestoreSubject: (SubjectSummary) -> Unit,
    onRestoreCourse: (CourseOverview) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = "Прошлые семестры и забытые курсы",
            title = "АРХИВ",
            onBackClick = onBack,
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
            item(key = "hint") {
                Text(
                    text = "Всё, что в архиве, не показывается на «Сегодня» и в «Предметах» и не попадает " +
                        "в статистику. Данные не удаляются — их можно вернуть в любой момент.",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            if (state.isEmpty) {
                item(key = "empty") {
                    Text(
                        text = "Архив пуст",
                        color = SlhTextMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                    )
                }
            }

            if (state.subjects.isNotEmpty()) {
                item(key = "subjects_label") {
                    SectionLabel(text = "ПРЕДМЕТЫ · ${state.subjects.size}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(items = state.subjects, key = { "subject_${it.id}" }) { subject ->
                    ArchiveRow(
                        title = subject.title,
                        subtitle = buildString {
                            append("семестр ${subject.semester}")
                            if (subject.totalLabs > 0) {
                                append(" · ${subject.doneLabs}/${subject.totalLabs} сделано")
                            }
                            if (subject.classroomCourseId != null) append(" · GC")
                        },
                        onClick = { onOpenSubject(subject.id) },
                        onRestore = { onRestoreSubject(subject) },
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                item(key = "subjects_space") {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (state.courses.isNotEmpty()) {
                item(key = "courses_label") {
                    SectionLabel(text = "КУРСЫ CLASSROOM · ${state.courses.size}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(items = state.courses, key = { "course_${it.id}" }) { course ->
                    ArchiveRow(
                        title = course.title,
                        subtitle = listOfNotNull(course.teacherName, "заданий: ${course.tasksCount}").joinToString(" · "),
                        onClick = { onOpenCourse(course.id) },
                        onRestore = { onRestoreCourse(course) },
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun ArchiveRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onRestore: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 12.dp, end = 8.dp, top = 11.dp, bottom = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SlhTextSecondary,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
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
                .border(1.dp, SlhOutline, RoundedCornerShape(6.dp))
                .clickable(role = Role.Button, onClick = onRestore)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "ВЕРНУТЬ",
                color = SlhAccent,
                fontFamily = SlhMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 640)
@Composable
private fun ArchiveScreenPreview() {
    StudentLabHubTheme {
        ArchiveScreen(
            state = ArchiveUiState(
                isLoading = false,
                subjects = listOf(
                    SubjectSummary(1, "Основы алгоритмизации", 2, 8, 8, 8, 8.0, archived = true),
                ),
                courses = listOf(
                    CourseOverview("c1", "Физкультура 2024", "Сидоров П. П.", 2, 0, 0, null, null, archived = true),
                ),
            ),
            onBack = {},
            onOpenSubject = {},
            onOpenCourse = {},
            onRestoreSubject = {},
            onRestoreCourse = {},
        )
    }
}
