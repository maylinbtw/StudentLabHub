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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.navigation.LaunchActions
import com.slh.studentlabhub.ui.auth.rememberGoogleAuthorization
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.components.SlhProgressDots
import com.slh.studentlabhub.ui.components.SlhPullRefresh
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

@Composable
fun SubjectsRoute(
    onSubjectClick: (subjectId: Long) -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenArchive: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubjectsViewModel = viewModel(factory = SubjectsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val addRequested by LaunchActions.addSubjectRequested.collectAsState()
    LaunchedEffect(addRequested) {
        if (addRequested) {
            showAddDialog = true
            LaunchActions.addSubjectRequested.value = false
        }
    }
    val authorize = rememberGoogleAuthorization(
        onAuthorized = viewModel::sync,
        onError = viewModel::onAuthorizationFailed,
    )

    SubjectsScreen(
        state = state,
        onAddSubjectClick = { showAddDialog = true },
        onSubjectClick = onSubjectClick,
        onOpenStatistics = onOpenStatistics,
        onOpenArchive = onOpenArchive,
        onArchiveSemester = viewModel::archiveSemester,
        onRefresh = {
            if (!state.isSyncing) {
                viewModel.onSyncStarted()
                authorize()
            }
        },
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
    onOpenStatistics: () -> Unit,
    onOpenArchive: () -> Unit,
    onArchiveSemester: (Int) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = "Лабораторные и практические",
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

        SlhPullRefresh(
            isRefreshing = state.isSyncing,
            onRefresh = onRefresh,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                state.errorMessage?.let { message ->
                    item(key = "sync_error") {
                        Text(
                            text = message,
                            color = SlhDanger,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (state.isEmpty) {
                    item(key = "empty") {
                        EmptyCard(onAddSubjectClick = onAddSubjectClick)
                    }
                } else {
                    item(key = "stats") {
                        StatsLink(
                            average = state.overallAverage,
                            done = state.doneWorks,
                            total = state.totalWorks,
                            onClick = onOpenStatistics,
                        )
                        Spacer(modifier = Modifier.height(22.dp))
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
                            onClick = { onSubjectClick(item.summary.id) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SlhProgressDots(total = item.summary.totalLabs, filled = item.summary.doneLabs)
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                if (!state.isEmpty) {
                    item(key = "add_subject") {
                        OutlineAction(text = "добавить предмет вручную", onClick = onAddSubjectClick)
                    }
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
}

@Composable
private fun StatsLink(
    average: Double?,
    done: Int,
    total: Int,
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
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Успеваемость",
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "сделано $done из $total · статистика ›",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
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
private fun SubjectCard(
    item: SubjectItem,
    onClick: () -> Unit,
) {
    val subject = item.summary
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
            Text(
                text = subject.title,
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            item.course?.teacherName?.let { teacher ->
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
            if (subject.redoCount > 0) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${subject.redoCount} на доработку",
                    color = SlhDanger,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        val average = subject.averageScore
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
            color = SlhContent,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Потяни вниз, чтобы загрузить курсы из Google Classroom — каждый станет предметом " +
                "со своими лабораторными и практическими. Или добавь предмет вручную.",
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

private fun worksLine(subject: SubjectSummary): String =
    if (subject.totalLabs == 0) {
        "работ пока нет — добавь внутри предмета"
    } else {
        "${subject.doneLabs}/${subject.totalLabs} сделано · ${subject.defendedLabs}/${subject.totalLabs} сдано"
    }

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 760)
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
                                summary = SubjectSummary(1, "ТРПО", 4, 20, 15, 12, 8.5, "c1", redoCount = 1),
                                course = CourseOverview("c1", "ПЗТ-44 ТРПО", "Иванова А. С.", 20, 12, 1, null, 8.3),
                            ),
                            SubjectItem(
                                summary = SubjectSummary(2, "ЗКИ", 4, 8, 5, 4, 7.5, "c2"),
                                course = null,
                            ),
                        ),
                    ),
                ),
                overallAverage = 8.0,
                totalWorks = 28,
                doneWorks = 20,
                archivedCount = 2,
            ),
            onAddSubjectClick = {},
            onSubjectClick = {},
            onOpenStatistics = {},
            onOpenArchive = {},
            onArchiveSemester = {},
            onRefresh = {},
        )
    }
}
