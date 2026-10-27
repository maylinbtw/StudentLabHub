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
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary

@Composable
fun EditSubjectDialog(
    initialTitle: String,
    initialSemester: Int,
    onDismiss: () -> Unit,
    onConfirm: (title: String, semester: Int, scheduleTitle: String?) -> Unit,
    initialScheduleTitle: String? = null,
    scheduleSubjects: List<String> = emptyList(),
) {
    var title by rememberSaveable { mutableStateOf(initialTitle) }
    var semester by rememberSaveable { mutableStateOf(initialSemester.toString()) }
    var scheduleTitle by rememberSaveable { mutableStateOf(initialScheduleTitle) }
    val scheduleOptions = remember(scheduleSubjects, initialScheduleTitle) {
        (scheduleSubjects + listOfNotNull(initialScheduleTitle)).distinct().sorted()
    }

    val semesterValue = semester.toIntOrNull()
    val isValid = title.isNotBlank() && semesterValue != null && semesterValue in 1..12

    SlhDialog(
        overline = Strings.get(R.string.lab_works),
        title = Strings.get(R.string.edit_title),
        onDismissRequest = onDismiss,
    ) {
        SlhLabeledField(
            label = Strings.get(R.string.field_title),
            value = title,
            onValueChange = { title = it.take(60) },
            capitalization = KeyboardCapitalization.Sentences,
        )
        Spacer(modifier = Modifier.height(14.dp))
        SlhLabeledField(
            label = Strings.get(R.string.field_semester),
            value = semester,
            onValueChange = { input -> semester = input.filter { it.isDigit() }.take(2) },
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            modifier = Modifier.width(120.dp),
        )
        if (scheduleOptions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = Strings.get(R.string.field_schedule_title),
                color = SlhTextMuted,
                fontFamily = SlhMono,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                ScheduleOption(
                    text = Strings.get(R.string.schedule_title_auto),
                    selected = scheduleTitle == null,
                    onClick = { scheduleTitle = null },
                )
                scheduleOptions.forEach { option ->
                    ScheduleOption(
                        text = option,
                        selected = scheduleTitle == option,
                        onClick = { scheduleTitle = option },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SlhDialogOutlinedButton(
                text = Strings.get(R.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
            SlhDialogPrimaryButton(
                text = Strings.get(R.string.action_save),
                enabled = isValid,
                onClick = {
                    if (semesterValue != null) {
                        onConfirm(title.trim(), semesterValue, scheduleTitle)
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ScheduleOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .border(1.5.dp, if (selected) SlhAccent else SlhTextMuted, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SlhAccent),
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = if (selected) SlhContent else SlhTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
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
            onConfirm = { _, _, _ -> },
            scheduleSubjects = listOf("Технология разработки программного обеспечения", "Компьютерные сети"),
        )
    }
}