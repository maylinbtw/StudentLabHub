package com.slh.studentlabhub.ui.screens.labdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.completedLabel
import com.slh.studentlabhub.ui.components.shortPrefix
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhInk
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOnAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

private data class StatusChipStyle(
    val leading: String,
    val label: String,
    val selectedContainer: Color,
    val selectedContent: Color,
    val selectedBorder: Color,
)

private fun LabStatus.chipStyle(type: WorkType): StatusChipStyle = when (this) {
    LabStatus.NOT_DONE -> StatusChipStyle(
        leading = "○",
        label = "Не сдел.",
        selectedContainer = SlhOutline,
        selectedContent = SlhWhite,
        selectedBorder = SlhTextMuted,
    )

    LabStatus.DONE -> StatusChipStyle(
        leading = "✓",
        label = "Сделано",
        selectedContainer = SlhWhite,
        selectedContent = SlhInk,
        selectedBorder = SlhWhite,
    )

    LabStatus.DEFENDED -> StatusChipStyle(
        leading = "★",
        label = type.completedLabel,
        selectedContainer = SlhAccent,
        selectedContent = SlhOnAccent,
        selectedBorder = SlhAccent,
    )
}

@Composable
fun LabDetailsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LabDetailsViewModel = viewModel(factory = LabDetailsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isFinished, state.isMissing) {
        if (state.isFinished || state.isMissing) {
            onBack()
        }
    }

    LabDetailsScreen(
        state = state,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onStatusChange = viewModel::onStatusChange,
        onScoreChange = viewModel::onScoreChange,
        onNotesChange = viewModel::onNotesChange,
        onSave = viewModel::save,
        onDelete = viewModel::delete,
        modifier = modifier,
    )
}

@Composable
fun LabDetailsScreen(
    state: LabDetailsUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onStatusChange: (LabStatus) -> Unit,
    onScoreChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showTitleEditor by rememberSaveable { mutableStateOf(false) }
    val prefix = state.type.shortPrefix

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = "Детали работы",
            title = if (state.isLoading) "" else "$prefix №${state.number}",
            onBackClick = onBack,
            actionIcon = SlhIcon.Trash,
            onActionClick = { showDeleteConfirm = true },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(role = Role.Button, onClick = { showTitleEditor = true }),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$prefix №${state.number} · ${state.title}",
                        color = SlhWhite,
                        fontFamily = SlhSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✎",
                        color = SlhTextMuted,
                        fontSize = 16.sp,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Предмет: ${state.subjectTitle}",
                    color = SlhTextMuted,
                    fontFamily = SlhSans,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                )

                Spacer(modifier = Modifier.height(22.dp))
                SectionLabel(
                    text = "СТАТУС · СДЕЛАНО / ${state.type.completedLabel.uppercase(Locale.getDefault())}",
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LabStatus.entries.forEach { status ->
                        StatusChip(
                            style = status.chipStyle(state.type),
                            selected = state.status == status,
                            onClick = { onStatusChange(status) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                if (state.hasScore) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionLabel(text = "ОЦЕНКА (БАЛЛ) · ВВОД ВРУЧНУЮ")
                    Spacer(modifier = Modifier.height(10.dp))
                    ScoreField(
                        value = state.scoreInput,
                        onValueChange = onScoreChange,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel(text = "ЗАМЕТКИ · ВОПРОСЫ ПРЕПОД.")
                Spacer(modifier = Modifier.height(10.dp))
                NotesField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                )

                Spacer(modifier = Modifier.height(28.dp))
                SaveButton(
                    enabled = state.canSave,
                    isSaving = state.isSaving,
                    onClick = onSave,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showTitleEditor) {
        TitleEditDialog(
            initialTitle = state.title,
            onDismiss = { showTitleEditor = false },
            onConfirm = { newTitle ->
                onTitleChange(newTitle)
                showTitleEditor = false
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SlhSurface,
            title = {
                Text(
                    text = "Удалить работу?",
                    color = SlhWhite,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = "$prefix №${state.number} · ${state.title} будет удалена вместе с заметками.",
                    color = SlhTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                ) {
                    Text(
                        text = "УДАЛИТЬ",
                        color = SlhAccent,
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
private fun StatusChip(
    style: StatusChipStyle,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    val containerColor = if (selected) style.selectedContainer else Color.Transparent
    val borderColor = if (selected) style.selectedBorder else SlhOutline
    val leadingColor = if (selected) style.selectedContent else SlhTextMuted
    val textColor = if (selected) style.selectedContent else SlhTextSecondary

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, borderColor, shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = style.leading,
            color = leadingColor,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = style.label,
            color = textColor,
            fontFamily = SlhSans,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun ScoreField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(8.dp)
    val isHighlighted = isFocused || value.isNotEmpty()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(84.dp)
                .height(58.dp)
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, if (isHighlighted) SlhAccent else SlhOutline, shape),
            contentAlignment = Alignment.Center,
        ) {
            if (value.isEmpty() && !isFocused) {
                Text(
                    text = "—",
                    color = SlhTextDisabled,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                interactionSource = interactionSource,
                textStyle = TextStyle(
                    color = SlhAccent,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(SlhAccent),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "/10",
            color = SlhTextMuted,
            fontFamily = SlhMono,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun NotesField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val shape = RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp, topEnd = 10.dp, bottomEnd = 10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(SlhAccent),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 160.dp)
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "— Вопросы преподавателя\n— Что доделать к защите",
                    color = SlhTextDisabled,
                    fontFamily = SlhSans,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = SlhTextSecondary,
                    fontFamily = SlhSans,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                ),
                cursorBrush = SolidColor(SlhAccent),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 132.dp),
            )
        }
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = 2.dp,
        bottomStart = 2.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(if (enabled || isSaving) SlhWhite else SlhOutline)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(SlhAccent),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = SlhInk,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "СОХРАНИТЬ",
                    color = if (enabled) SlhInk else SlhTextMuted,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
    }
}

@Composable
private fun TitleEditDialog(
    initialTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var draft by rememberSaveable { mutableStateOf(initialTitle) }
    val shape = RoundedCornerShape(8.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlhSurface,
        title = {
            Text(
                text = "Название работы",
                color = SlhWhite,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            BasicTextField(
                value = draft,
                onValueChange = { draft = it.take(80) },
                singleLine = true,
                textStyle = TextStyle(
                    color = SlhWhite,
                    fontFamily = SlhSans,
                    fontSize = 15.sp,
                ),
                cursorBrush = SolidColor(SlhAccent),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(SlhBackground)
                    .border(1.dp, SlhAccent, shape)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(draft.trim()) },
                enabled = draft.isNotBlank(),
            ) {
                Text(
                    text = "ГОТОВО",
                    color = if (draft.isNotBlank()) SlhAccent else SlhTextDisabled,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "ОТМЕНА",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 640)
@Composable
private fun LabDetailsLabPreview() {
    StudentLabHubTheme {
        LabDetailsScreen(
            state = LabDetailsUiState(
                isLoading = false,
                subjectTitle = "ТРПО",
                type = WorkType.LAB,
                number = 4,
                title = "Выбор модели ЖЦ",
                status = LabStatus.DEFENDED,
                notes = "— Почему инкрементная модель?\n— Обосновать таблицы 1–4",
            ),
            onBack = {},
            onTitleChange = {},
            onStatusChange = {},
            onScoreChange = {},
            onNotesChange = {},
            onSave = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 780)
@Composable
private fun LabDetailsPracticePreview() {
    StudentLabHubTheme {
        LabDetailsScreen(
            state = LabDetailsUiState(
                isLoading = false,
                subjectTitle = "ТРПО",
                type = WorkType.PRACTICE,
                number = 2,
                title = "Диаграмма классов",
                status = LabStatus.DEFENDED,
                scoreInput = "8",
            ),
            onBack = {},
            onTitleChange = {},
            onStatusChange = {},
            onScoreChange = {},
            onNotesChange = {},
            onSave = {},
            onDelete = {},
        )
    }
}