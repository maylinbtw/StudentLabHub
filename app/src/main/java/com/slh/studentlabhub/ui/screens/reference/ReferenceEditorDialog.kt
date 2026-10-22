package com.slh.studentlabhub.ui.screens.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefCategoryIds
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhDialog
import com.slh.studentlabhub.ui.components.SlhDialogOutlinedButton
import com.slh.studentlabhub.ui.components.SlhDialogPrimaryButton
import com.slh.studentlabhub.ui.components.SlhLabeledField
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

@Composable
fun ReferenceEditorDialog(
    initial: RefEntry?,
    defaultTech: RefTech,
    categories: List<RefCategory>,
    onDismiss: () -> Unit,
    onSave: (RefEntryDraft) -> Unit,
    onDelete: (() -> Unit)?,
    defaultCategoryId: String? = null,
) {
    var tech by rememberSaveable { mutableStateOf(initial?.tech ?: defaultTech) }
    var categoryId by rememberSaveable {
        mutableStateOf(
            initial?.categoryId
                ?: defaultCategoryId?.takeIf { it != RefCategoryIds.TLDR_LINUX },
        )
    }
    var title by rememberSaveable { mutableStateOf(initial?.title.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(initial?.description.orEmpty()) }
    var example by rememberSaveable { mutableStateOf(initial?.example.orEmpty()) }
    var confirmDelete by remember { mutableStateOf(false) }

    val techCategories = categories.filter {
        it.tech == tech &&
            it.id != RefCategoryIds.TLDR_LINUX &&
            it.id != RefCategoryIds.custom(tech)
    }
    val isValid = title.isNotBlank()

    SlhDialog(
        overline = "Справочник",
        title = if (initial == null) "НОВАЯ СТАТЬЯ" else "ИЗМЕНИТЬ СТАТЬЮ",
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 460.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionLabel(text = "ТЕХНОЛОГИЯ")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                RefTech.entries.forEach { option ->
                    SelectChip(
                        text = option.label,
                        selected = option == tech,
                        onClick = {
                            if (option != tech) {
                                tech = option
                                categoryId = null
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionLabel(text = "РАЗДЕЛ")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SelectChip(
                    text = "Мои заметки",
                    selected = categoryId == null || categoryId == RefCategoryIds.custom(tech),
                    onClick = { categoryId = null },
                )
                techCategories.forEach { category ->
                    SelectChip(
                        text = category.title,
                        selected = categoryId == category.id,
                        onClick = { categoryId = category.id },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SlhLabeledField(
                label = "КОМАНДА / КОНСТРУКЦИЯ",
                value = title,
                onValueChange = { title = it.take(60) },
            )
            Spacer(modifier = Modifier.height(12.dp))
            SlhLabeledField(
                label = "ОПИСАНИЕ",
                value = description,
                onValueChange = { description = it.take(160) },
                capitalization = KeyboardCapitalization.Sentences,
            )
            Spacer(modifier = Modifier.height(12.dp))
            SectionLabel(text = "ПРИМЕР")
            Spacer(modifier = Modifier.height(6.dp))
            MultilineField(
                value = example,
                onValueChange = { example = it.take(1500) },
            )

            if (onDelete != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(role = Role.Button, onClick = {
                            if (confirmDelete) onDelete() else confirmDelete = true
                        })
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                ) {
                    Text(
                        text = if (confirmDelete) "НАЖМИ ЕЩЁ РАЗ, ЧТОБЫ УДАЛИТЬ" else "УДАЛИТЬ СТАТЬЮ",
                        color = SlhDanger,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SlhDialogOutlinedButton(
                text = "ОТМЕНА",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
            SlhDialogPrimaryButton(
                text = "СОХРАНИТЬ",
                enabled = isValid,
                onClick = {
                    onSave(
                        RefEntryDraft(
                            id = initial?.id,
                            tech = tech,
                            categoryId = categoryId?.takeIf { it != RefCategoryIds.custom(tech) },
                            title = title,
                            description = description,
                            example = example,
                        ),
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MultilineField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .clip(shape)
            .background(SlhBackground)
            .border(1.dp, if (isFocused) SlhAccent else SlhOutline, shape)
            .padding(12.dp),
        contentAlignment = Alignment.TopStart,
    ) {
        if (value.isEmpty()) {
            Text(
                text = "chmod 755 script.sh",
                color = SlhTextDisabled,
                fontFamily = SlhMono,
                fontSize = 13.sp,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            textStyle = TextStyle(
                color = SlhContent,
                fontFamily = SlhMono,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            ),
            cursorBrush = SolidColor(SlhAccent),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 720)
@Composable
private fun ReferenceEditorDialogPreview() {
    StudentLabHubTheme {
        ReferenceEditorDialog(
            initial = null,
            defaultTech = RefTech.LINUX,
            categories = listOf(
                RefCategory("linux_files", RefTech.LINUX, "Файлы и директории", listOf("ls")),
                RefCategory("linux_network", RefTech.LINUX, "Сеть", listOf("ping")),
            ),
            onDismiss = {},
            onSave = {},
            onDelete = null,
        )
    }
}
