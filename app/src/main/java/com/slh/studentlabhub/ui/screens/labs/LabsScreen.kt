package com.slh.studentlabhub.ui.screens.labs

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
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.components.SlhProgressDots
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

@Composable
fun LabsRoute(
    onSubjectClick: (SubjectSummary) -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LabsViewModel = viewModel(factory = LabsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    LabsScreen(
        state = state,
        onAddSubjectClick = { showAddDialog = true },
        onSubjectClick = onSubjectClick,
        onStatisticsClick = onStatisticsClick,
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
fun LabsScreen(
    state: LabsUiState,
    onAddSubjectClick: () -> Unit,
    onSubjectClick: (SubjectSummary) -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = "Учёт и оценки",
            title = "ЛАБОРАТОРНЫЕ",
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                if (state.sections.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = "Пока нет предметов.\nДобавь первый, чтобы начать учёт работ.",
                            color = SlhTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                        )
                    }
                }
                state.sections.forEach { section ->
                    item(key = "semester_${section.semester}") {
                        SectionLabel(text = "СЕМЕСТР ${section.semester}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(items = section.subjects, key = { it.id }) { subject ->
                        SubjectCard(
                            subject = subject,
                            onClick = { onSubjectClick(subject) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SlhProgressDots(total = subject.totalLabs, filled = subject.doneLabs)
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
                item(key = "add_subject") {
                    AddSubjectButton(onClick = onAddSubjectClick)
                }
                item(key = "statistics") {
                    Spacer(modifier = Modifier.height(18.dp))
                    StatisticsButton(onClick = onStatisticsClick)
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectSummary,
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
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject.title,
                color = SlhWhite,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${subject.doneLabs}/${subject.totalLabs} сделано · " +
                        "${subject.defendedLabs}/${subject.totalLabs} защищено",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        DotMatrixText(
            text = subject.averageScore?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
            color = SlhAccent,
        )
    }
}

@Composable
private fun AddSubjectButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlhIconView(icon = SlhIcon.Plus, color = SlhTextMuted, size = 12.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "добавить предмет",
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
        )
    }
}

@Composable
private fun StatisticsButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(shape)
            .border(1.dp, SlhAccent, shape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SlhIconView(icon = SlhIcon.Menu, color = SlhAccent, size = 14.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Статистика успеваемости",
            color = SlhAccent,
            fontFamily = SlhSans,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
        )
    }
}

private val previewState = LabsUiState(
    isLoading = false,
    sections = listOf(
        SemesterSection(
            semester = 4,
            subjects = listOf(
                SubjectSummary(1, "ТРПО", 4, 9, 7, 5, 8.5),
                SubjectSummary(2, "Операционные системы", 4, 6, 5, 4, 7.5),
                SubjectSummary(3, "Программирование Java", 4, 5, 5, 5, 9.0),
            ),
        ),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 780)
@Composable
private fun LabsScreenPreview() {
    StudentLabHubTheme {
        LabsScreen(
            state = previewState,
            onAddSubjectClick = {},
            onSubjectClick = {},
            onStatisticsClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 640)
@Composable
private fun AddSubjectDialogPreview() {
    StudentLabHubTheme {
        AddSubjectDialog(onDismiss = {}, onConfirm = { _, _, _, _ -> })
    }
}