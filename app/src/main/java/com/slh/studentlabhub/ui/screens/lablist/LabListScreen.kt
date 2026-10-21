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
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.theme.SlhBlack
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhRed
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
        onDeleteSubject = viewModel::deleteSubject,
        modifier = modifier,
    )
}

@Composable
fun LabListScreen(
    state: LabListUiState,
    onBack: () -> Unit,
    onLabClick: (Lab) -> Unit,
    onDeleteSubject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    val details = state.details

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBlack),
    ) {
        ScreenHeader(
            overline = "Лабораторные работы",
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
                                text = "Удалить предмет",
                                color = SlhRed,
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
                        color = SlhRed,
                        strokeWidth = 2.dp,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                ) {
                    item(key = "stats") {
                        StatsRow(summary = details.summary)
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                    if (details.labs.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = "В этом предмете пока нет лабораторных работ",
                                color = SlhTextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                            )
                        }
                    }
                    items(items = details.labs, key = { it.id }) { lab ->
                        LabRow(
                            lab = lab,
                            onClick = { onLabClick(lab) },
                        )
                        HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    }
                }
            }
        }
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
                    text = "Предмет «${details?.summary?.title.orEmpty()}» и все его лабораторные работы будут удалены без возможности восстановления.",
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
                        color = SlhRed,
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
private fun StatsRow(summary: SubjectSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatColumn(
            value = summary.averageScore?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
            label = "СР. БАЛЛ",
            valueColor = SlhRed,
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
        Text(
            text = value,
            color = valueColor,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
        )
        Spacer(modifier = Modifier.height(2.dp))
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
            text = "ЛР №${lab.number} · ${lab.title}",
            color = if (isActive) SlhWhite else SlhTextMuted,
            fontFamily = SlhSans,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (lab.score != null && isActive) {
            Spacer(modifier = Modifier.width(12.dp))
            ScoreChip(score = lab.score, defended = lab.status == LabStatus.DEFENDED)
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
                modifier = baseModifier.background(SlhRed),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "★", color = SlhWhite, fontSize = 12.sp)
            }
        }

        LabStatus.DONE -> {
            Box(
                modifier = baseModifier.background(SlhWhite),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "✓", color = SlhBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
    defended: Boolean,
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 30.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (defended) SlhRed else SlhDotInactive)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = score.toString(),
            color = SlhWhite,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

private val previewDetails = SubjectDetails(
    summary = SubjectSummary(
        id = 1,
        title = "ТРПО",
        semester = 4,
        totalLabs = 8,
        doneLabs = 5,
        defendedLabs = 3,
        averageScore = 8.33,
    ),
    labs = listOf(
        Lab(1, 1, 1, "Анализ задачи", LabStatus.DEFENDED, 9, "", null),
        Lab(2, 1, 2, "Требования", LabStatus.DEFENDED, 8, "", null),
        Lab(3, 1, 3, "Функц. требования", LabStatus.DEFENDED, 8, "", null),
        Lab(4, 1, 4, "Модель ЖЦ", LabStatus.DONE, 9, "", null),
        Lab(5, 1, 5, "Структурное пр.", LabStatus.NOT_DONE, null, "", null),
        Lab(6, 1, 6, "UX-прототипы", LabStatus.NOT_DONE, null, "", null),
        Lab(7, 1, 7, "UI-прототипы", LabStatus.NOT_DONE, null, "", null),
        Lab(8, 1, 8, "Тестирование", LabStatus.NOT_DONE, null, "", null),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 780)
@Composable
private fun LabListScreenPreview() {
    StudentLabHubTheme {
        LabListScreen(
            state = LabListUiState(isLoading = false, details = previewDetails),
            onBack = {},
            onLabClick = {},
            onDeleteSubject = {},
        )
    }
}