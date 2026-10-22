package com.slh.studentlabhub.ui.screens.lablist

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.domain.model.CourseOverview
import com.slh.studentlabhub.ui.components.SlhDialog
import com.slh.studentlabhub.ui.components.SlhDialogOutlinedButton
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

@Composable
fun CoursePickerDialog(
    subjectTitle: String,
    courses: List<CourseOverview>,
    currentCourseId: String?,
    onDismiss: () -> Unit,
    onPick: (courseId: String) -> Unit,
) {
    SlhDialog(
        overline = "Google Classroom",
        title = "СВЯЗАТЬ С КУРСОМ",
        onDismissRequest = onDismiss,
    ) {
        Text(
            text = "Предмет: $subjectTitle",
            color = SlhTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (courses.isEmpty()) {
            Text(
                text = "Свободных курсов нет. Синхронизируй Classroom на вкладке «Сегодня» — " +
                    "или все курсы уже связаны с другими предметами.",
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                courses.forEach { course ->
                    CourseOption(
                        course = course,
                        isSelected = course.id == currentCourseId,
                        onClick = { onPick(course.id) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SlhDialogOutlinedButton(
            text = "ОТМЕНА",
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CourseOption(
    course: CourseOverview,
    isSelected: Boolean,
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
                text = course.title,
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            course.teacherName?.let { teacher ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = teacher,
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 560)
@Composable
private fun CoursePickerDialogPreview() {
    StudentLabHubTheme {
        CoursePickerDialog(
            subjectTitle = "ТРПО",
            courses = listOf(
                CourseOverview("1", "ПЗТ-44 ТРПО", "Иванова А. С.", 8, 5, 0, null, 8.3),
                CourseOverview("2", "Криптография", null, 6, 2, 0, null, null),
            ),
            currentCourseId = null,
            onDismiss = {},
            onPick = {},
        )
    }
}
