package com.slh.studentlabhub.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.components.ScreenHeader
import com.slh.studentlabhub.ui.components.SectionLabel
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhDotShape
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme
import java.util.Locale
import kotlin.math.roundToInt

private const val SCORE_DOTS = 10
private const val MAX_PROGRESS_DOTS = 24

@Composable
fun StatisticsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    StatisticsScreen(
        state = state,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun StatisticsScreen(
    state: StatisticsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBackground),
    ) {
        ScreenHeader(
            overline = "Успеваемость",
            title = "СТАТИСТИКА",
            onBackClick = onBack,
        )

        when {
            state.isLoading -> {
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
            }

            state.subjects.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Пока нет данных.\nДобавь предметы и отметь первые работы.",
                        color = SlhTextMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    SectionLabel(text = "СРЕДНИЙ БАЛЛ ПО ПРЕДМЕТАМ")
                    Spacer(modifier = Modifier.height(20.dp))
                    SubjectColumns(subjects = state.subjects)

                    Spacer(modifier = Modifier.height(28.dp))
                    OverallCard(average = state.overallAverage)

                    Spacer(modifier = Modifier.height(22.dp))
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    Spacer(modifier = Modifier.height(18.dp))

                    SectionLabel(text = "ПРОГРЕСС · СДЕЛАНО / ЗАЩИЩЕНО")
                    Spacer(modifier = Modifier.height(14.dp))
                    ProgressBlock(
                        label = "Защищено (${state.defendedWorks}/${state.totalWorks})",
                        filled = state.defendedWorks,
                        total = state.totalWorks,
                        color = SlhAccent,
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    ProgressBlock(
                        label = "Сделано (${state.doneWorks}/${state.totalWorks})",
                        filled = state.doneWorks,
                        total = state.totalWorks,
                        color = SlhContent,
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(thickness = 1.dp, color = SlhOutline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Legend(
                        defended = state.defendedWorks,
                        done = state.doneWorks,
                        remaining = state.remainingWorks,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SubjectColumns(subjects: List<SubjectScore>) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val minColumnWidth = 72.dp
        val evenWidth = maxWidth / subjects.size
        val columnWidth = if (evenWidth < minColumnWidth) minColumnWidth else evenWidth

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            subjects.forEach { subject ->
                ScoreColumn(
                    subject = subject,
                    width = columnWidth,
                )
            }
        }
    }
}

@Composable
private fun ScoreColumn(
    subject: SubjectScore,
    width: Dp,
) {
    val litDots = subject.averageScore
        ?.roundToInt()
        ?.coerceIn(0, SCORE_DOTS)
        ?: 0

    Column(
        modifier = Modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(SCORE_DOTS) { index ->
                val isLit = index >= SCORE_DOTS - litDots
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(SlhDotShape)
                        .background(if (isLit) SlhAccent else SlhDotInactive),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = subject.shortTitle,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subject.averageScore?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
            color = SlhTextMuted,
            fontFamily = SlhMono,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun OverallCard(average: Double?) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(SlhAccent),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, SlhOutline, shape)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Общий средний балл",
                color = SlhContent,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            DotMatrixText(
                text = average?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
                color = SlhAccent,
                dotSize = 4.5.dp,
                dotGap = 1.dp,
            )
        }
    }
}

@Composable
private fun ProgressBlock(
    label: String,
    filled: Int,
    total: Int,
    color: Color,
) {
    Column {
        Text(
            text = label,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        DotBar(
            filled = filled,
            total = total,
            color = color,
        )
    }
}

@Composable
private fun DotBar(
    filled: Int,
    total: Int,
    color: Color,
) {
    if (total <= 0) return
    val dotsCount = total.coerceAtMost(MAX_PROGRESS_DOTS)
    val litCount = if (total <= MAX_PROGRESS_DOTS) {
        filled
    } else {
        (filled.toDouble() / total * MAX_PROGRESS_DOTS).roundToInt()
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(dotsCount) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(SlhDotShape)
                    .background(if (index < litCount) color else SlhDotInactive),
            )
        }
    }
}

@Composable
private fun Legend(
    defended: Int,
    done: Int,
    remaining: Int,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        LegendItem(symbol = "★", text = "Защ: $defended", symbolColor = SlhAccent)
        LegendItem(symbol = "✓", text = "Сдел: $done", symbolColor = SlhContent)
        LegendItem(symbol = "○", text = "Ост: $remaining", symbolColor = SlhTextMuted)
    }
}

@Composable
private fun LegendItem(
    symbol: String,
    text: String,
    symbolColor: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = symbol,
            color = symbolColor,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = SlhTextSecondary,
            fontFamily = SlhMono,
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 820)
@Composable
private fun StatisticsScreenPreview() {
    StudentLabHubTheme {
        StatisticsScreen(
            state = StatisticsUiState(
                isLoading = false,
                subjects = listOf(
                    SubjectScore(1, "ТРПО", 8.5),
                    SubjectScore(2, "ОС", 7.5),
                    SubjectScore(3, "Java", 9.0),
                ),
                overallAverage = 8.33,
                totalWorks = 20,
                doneWorks = 16,
                defendedWorks = 13,
            ),
            onBack = {},
        )
    }
}