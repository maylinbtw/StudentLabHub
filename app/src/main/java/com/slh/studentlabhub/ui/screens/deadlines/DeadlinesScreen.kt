package com.slh.studentlabhub.ui.screens.deadlines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.theme.SlhBlack
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhRed
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale

data class CourseUi(
    val id: Long,
    val title: String,
    val tasksCount: Int,
    val completedTasks: Int,
    val nearestDeadline: String,
    val averageScore: Double,
)

val sampleCourses = listOf(
    CourseUi(
        id = 1,
        title = "ТРПО",
        tasksCount = 8,
        completedTasks = 5,
        nearestDeadline = "15.07",
        averageScore = 8.3,
    ),
    CourseUi(
        id = 2,
        title = "Операционные системы",
        tasksCount = 5,
        completedTasks = 3,
        nearestDeadline = "20.07",
        averageScore = 7.5,
    ),
    CourseUi(
        id = 3,
        title = "Программирование Java",
        tasksCount = 6,
        completedTasks = 5,
        nearestDeadline = "25.07",
        averageScore = 9.0,
    ),
)

@Composable
fun DeadlinesScreen(
    courses: List<CourseUi>,
    onCourseClick: (CourseUi) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSyncing: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBlack),
    ) {
        ScreenHeader(
            overline = "Google Classroom",
            title = "ДЕДЛАЙНЫ",
            actionIcon = SlhIcon.Refresh,
            onActionClick = onSyncClick,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
        ) {
            item {
                SectionLabel(text = "МОИ КУРСЫ")
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(items = courses, key = { _, course -> course.id }) { index, course ->
                CourseCard(
                    course = course,
                    highlighted = index == 0,
                    onClick = { onCourseClick(course) },
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProgressDots(total = course.tasksCount, filled = course.completedTasks)
                Spacer(modifier = Modifier.height(14.dp))
            }
            item {
                SyncButton(isSyncing = isSyncing, onClick = onSyncClick)
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: CourseUi,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        if (highlighted) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(SlhRed),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, SlhOutline, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    color = SlhWhite,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${course.tasksCount} ${tasksWord(course.tasksCount)} · дедлайн ${course.nearestDeadline}",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = String.format(Locale.US, "%.1f", course.averageScore),
                color = SlhRed,
                style = MaterialTheme.typography.displaySmall,
            )
        }
    }
}

@Composable
private fun ProgressDots(
    total: Int,
    filled: Int,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (index < filled) SlhWhite else SlhDotInactive),
            )
        }
    }
}

@Composable
private fun SyncButton(
    isSyncing: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(enabled = !isSyncing, role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSyncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = SlhTextMuted,
                strokeWidth = 1.5.dp,
            )
        } else {
            SlhIconView(icon = SlhIcon.Refresh, color = SlhTextMuted, size = 14.dp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "синхронизировать с GC",
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
        )
    }
}

private fun tasksWord(count: Int): String {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> "заданий"
        mod10 == 1 -> "задание"
        mod10 in 2..4 -> "задания"
        else -> "заданий"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 780)
@Composable
private fun DeadlinesScreenPreview() {
    StudentLabHubTheme {
        DeadlinesScreen(
            courses = sampleCourses,
            onCourseClick = {},
            onSyncClick = {},
        )
    }
}