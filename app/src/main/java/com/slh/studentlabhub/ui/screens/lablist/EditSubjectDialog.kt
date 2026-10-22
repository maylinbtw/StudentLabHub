package com.slh.studentlabhub.ui.screens.lablist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.ui.components.SlhDialog
import com.slh.studentlabhub.ui.components.SlhDialogOutlinedButton
import com.slh.studentlabhub.ui.components.SlhDialogPrimaryButton
import com.slh.studentlabhub.ui.components.SlhLabeledField
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

@Composable
fun EditSubjectDialog(
    initialTitle: String,
    initialSemester: Int,
    onDismiss: () -> Unit,
    onConfirm: (title: String, semester: Int) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(initialTitle) }
    var semester by rememberSaveable { mutableStateOf(initialSemester.toString()) }

    val semesterValue = semester.toIntOrNull()
    val isValid = title.isNotBlank() && semesterValue != null && semesterValue in 1..12

    SlhDialog(
        overline = "Лабораторные",
        title = "РЕДАКТИРОВАТЬ",
        onDismissRequest = onDismiss,
    ) {
        SlhLabeledField(
            label = "НАЗВАНИЕ",
            value = title,
            onValueChange = { title = it.take(60) },
            capitalization = KeyboardCapitalization.Sentences,
        )
        Spacer(modifier = Modifier.height(14.dp))
        SlhLabeledField(
            label = "СЕМЕСТР",
            value = semester,
            onValueChange = { input -> semester = input.filter { it.isDigit() }.take(2) },
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            modifier = Modifier.width(120.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
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
                    if (semesterValue != null) {
                        onConfirm(title.trim(), semesterValue)
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 480)
@Composable
private fun EditSubjectDialogPreview() {
    StudentLabHubTheme {
        EditSubjectDialog(
            initialTitle = "ТРПО",
            initialSemester = 4,
            onDismiss = {},
            onConfirm = { _, _ -> },
        )
    }
}