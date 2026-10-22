package com.slh.studentlabhub.ui.screens.coursetasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.ui.components.SlhDialog
import com.slh.studentlabhub.ui.components.SlhDialogOutlinedButton
import com.slh.studentlabhub.ui.components.SlhDialogPrimaryButton
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

@Composable
fun SubjectPickerDialog(
    courseTitle: String,
    subjects: List<SubjectSummary>,
    currentSubjectId: Long?,
    onDismiss: () -> Unit,
    onPick: (subjectId: Long) -> Unit,
    onCreate: () -> Unit,
) {
    val sorted = remember(subjects, courseTitle) {
        subjects
            .map { it to isSimilar(courseTitle, it.title) }
            .sortedByDescending { it.second }
    }

    SlhDialog(
        overline = "Лабораторные",
        title = "СВЯЗАТЬ С ПРЕДМЕТОМ",
        onDismissRequest = onDismiss,
    ) {
        Text(
            text = "Курс Classroom: $courseTitle",
            color = SlhTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (sorted.isEmpty()) {
            Text(
                text = "Во «Лабораторных» пока нет предметов. Создай новый — он сразу будет связан с курсом.",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                sorted.forEach { (subject, similar) ->
                    SubjectOption(
                        subject = subject,
                        isSelected = subject.id == currentSubjectId,
                        isSimilar = similar,
                        onClick = { onPick(subject.id) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SlhDialogOutlinedButton(
                text = "ОТМЕНА",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
            SlhDialogPrimaryButton(
                text = "НОВЫЙ",
                enabled = true,
                onClick = onCreate,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SubjectOption(
    subject: SubjectSummary,
    isSelected: Boolean,
    isSimilar: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, if (isSelected) SlhAccent else SlhOutline, shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .border(1.5.dp, if (isSelected) SlhAccent else SlhTextMuted, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SlhAccent),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject.title,
                color = SlhWhite,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = buildString {
                    append("семестр ${subject.semester}")
                    if (isSimilar) append(" · похоже на этот курс")
                },
                color = if (isSimilar) SlhAccent else SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun words(text: String): Set<String> =
    text.lowercase()
        .split(' ', '-', '_', '.', ',', '"', '«', '»', '(', ')')
        .map { word -> word.filter { it.isLetterOrDigit() } }
        .filter { it.length >= 2 }
        .toSet()

private fun isSimilar(courseTitle: String, subjectTitle: String): Boolean {
    val course = courseTitle.lowercase()
    val subject = subjectTitle.lowercase().trim()
    if (subject.isNotEmpty() && course.contains(subject)) return true
    val common = words(courseTitle).intersect(words(subjectTitle))
    return common.any { it.length >= 3 }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 560)
@Composable
private fun SubjectPickerDialogPreview() {
    StudentLabHubTheme {
        SubjectPickerDialog(
            courseTitle = "ПЗТ-44 ТРПО",
            subjects = listOf(
                SubjectSummary(1, "ТРПО", 4, 9, 7, 5, 8.5),
                SubjectSummary(2, "Операционные системы", 4, 6, 5, 4, 7.5),
            ),
            currentSubjectId = null,
            onDismiss = {},
            onPick = {},
            onCreate = {},
        )
    }
}
