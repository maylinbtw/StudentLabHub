package com.slh.studentlabhub.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.ui.theme.SlhPalette

private object DotMatrixFont {
    private val dash = listOf(".....", ".....", ".....", "#####", ".....", ".....", ".....")

    val glyphs: Map<Char, List<String>> = mapOf(
        '0' to listOf(".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
        '1' to listOf("..#..", ".##..", "..#..", "..#..", "..#..", "..#..", ".###."),
        '2' to listOf(".###.", "#...#", "....#", "...#.", "..#..", ".#...", "#####"),
        '3' to listOf("#####", "...#.", "..#..", "...#.", "....#", "#...#", ".###."),
        '4' to listOf("...#.", "..##.", ".#.#.", "#..#.", "#####", "...#.", "...#."),
        '5' to listOf("#####", "#....", "####.", "....#", "....#", "#...#", ".###."),
        '6' to listOf("..##.", ".#...", "#....", "####.", "#...#", "#...#", ".###."),
        '7' to listOf("#####", "....#", "...#.", "..#..", ".#...", ".#...", ".#..."),
        '8' to listOf(".###.", "#...#", "#...#", ".###.", "#...#", "#...#", ".###."),
        '9' to listOf(".###.", "#...#", "#...#", ".####", "....#", "...#.", ".##.."),
        '.' to listOf(".", ".", ".", ".", ".", ".", "#"),
        ',' to listOf(".", ".", ".", ".", ".", ".", "#"),
        '/' to listOf("....#", "....#", "...#.", "..#..", ".#...", "#....", "#...."),
        '-' to dash,
        '—' to dash,
        ' ' to listOf("..", "..", "..", "..", "..", "..", ".."),
        'S' to listOf(".####", "#....", "#....", ".###.", "....#", "....#", "####."),
        'L' to listOf("#....", "#....", "#....", "#....", "#....", "#....", "#####"),
        'H' to listOf("#...#", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
    )
}

private const val GLYPH_ROWS = 7

@Composable
fun DotMatrixText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 3.5.dp,
    dotGap: Dp = 0.75.dp,
    offColor: Color = Color.Transparent,
) {
    val glyphs = remember(text) { text.mapNotNull { DotMatrixFont.glyphs[it] } }
    val columns = remember(glyphs) {
        if (glyphs.isEmpty()) 0 else glyphs.sumOf { it.first().length } + glyphs.size - 1
    }
    val cell = dotSize + dotGap
    val width = if (columns == 0) 0.dp else cell * columns - dotGap
    val height = cell * GLYPH_ROWS - dotGap
    val roundDots = SlhPalette.roundDots

    Canvas(
        modifier = modifier
            .size(width = width, height = height)
            .semantics { contentDescription = text },
    ) {
        val cellPx = cell.toPx()
        val radius = dotSize.toPx() / 2f
        var startColumn = 0
        glyphs.forEach { glyph ->
            glyph.forEachIndexed { row, line ->
                line.forEachIndexed { column, mark ->
                    val dotColor = if (mark == '#') color else offColor
                    if (dotColor.alpha > 0f) {
                        val left = (startColumn + column) * cellPx
                        val top = row * cellPx
                        if (roundDots) {
                            drawCircle(
                                color = dotColor,
                                radius = radius,
                                center = Offset(x = left + radius, y = top + radius),
                            )
                        } else {
                            drawRect(
                                color = dotColor,
                                topLeft = Offset(x = left, y = top),
                                size = Size(width = radius * 2f, height = radius * 2f),
                            )
                        }
                    }
                }
            }
            startColumn += glyph.first().length + 1
        }
    }
}