package com.slh.studentlabhub.ui.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhOnContent
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhContent

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, semester: Int, labsCount: Int, practiceCount: Int) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var semester by rememberSaveable { mutableStateOf("4") }
    var labsCount by rememberSaveable { mutableStateOf("6") }
    var practiceCount by rememberSaveable { mutableStateOf("0") }

    val semesterValue = semester.toIntOrNull()
    val labsValue = labsCount.toIntOrNull()
    val practiceValue = practiceCount.toIntOrNull()
    val isValid = title.isNotBlank() &&
            semesterValue != null && semesterValue in 1..12 &&
            labsValue != null && labsValue in 0..30 &&
            practiceValue != null && practiceValue in 0..30

    val shape = RoundedCornerShape(12.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, SlhOutline, shape)
                .padding(20.dp),
        ) {
            Text(
                text = "Лабораторные",
                color = SlhAccent,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "НОВЫЙ ПРЕДМЕТ",
                color = SlhContent,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(20.dp))
            SlhTextField(
                label = "НАЗВАНИЕ",
                value = title,
                onValueChange = { title = it.take(60) },
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
            )
            Spacer(modifier = Modifier.height(14.dp))
            SlhTextField(
                label = "СЕМЕСТР",
                value = semester,
                onValueChange = { input -> semester = input.filter { it.isDigit() }.take(2) },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.width(120.dp),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SlhTextField(
                    label = "ЛАБОРАТОРНЫХ",
                    value = labsCount,
                    onValueChange = { input -> labsCount = input.filter { it.isDigit() }.take(2) },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
                SlhTextField(
                    label = "ПРАКТИЧЕСКИХ",
                    value = practiceCount,
                    onValueChange = { input -> practiceCount = input.filter { it.isDigit() }.take(2) },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DialogOutlinedButton(
                    text = "ОТМЕНА",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                DialogPrimaryButton(
                    text = "ДОБАВИТЬ",
                    enabled = isValid,
                    onClick = {
                        if (semesterValue != null && labsValue != null && practiceValue != null) {
                            onConfirm(title, semesterValue, labsValue, practiceValue)
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SlhTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = modifier) {
        SectionLabel(text = label)
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            interactionSource = interactionSource,
            textStyle = TextStyle(
                color = SlhContent,
                fontFamily = SlhMono,
                fontSize = 14.sp,
            ),
            cursorBrush = SolidColor(SlhAccent),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(SlhBackground)
                .border(1.dp, if (isFocused) SlhAccent else SlhOutline, shape)
                .padding(horizontal = 12.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun DialogOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun DialogPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        topStart = 2.dp,
        bottomStart = 2.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp,
    )
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(if (enabled) SlhContent else SlhOutline)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(if (enabled) SlhAccent else SlhTextDisabled),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = if (enabled) SlhOnContent else SlhTextMuted,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}