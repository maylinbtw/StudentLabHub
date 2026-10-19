package com.slh.studentlabhub.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SlhIcon { Grid, Diamond, Menu, Square, Gear, Refresh, Back, More }

@Composable
fun SlhIconView(
    icon: SlhIcon,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val u = this.size.width / 18f
        when (icon) {
            SlhIcon.Grid -> drawGridIcon(color, u)
            SlhIcon.Diamond -> drawDiamondIcon(color, u)
            SlhIcon.Menu -> drawMenuIcon(color, u)
            SlhIcon.Square -> drawSquareIcon(color, u)
            SlhIcon.Gear -> drawGearIcon(color, u)
            SlhIcon.Refresh -> drawRefreshIcon(color, u)
            SlhIcon.Back -> drawBackIcon(color, u)
            SlhIcon.More -> drawMoreIcon(color, u)
        }
    }
}

private fun DrawScope.drawGridIcon(color: Color, u: Float) {
    val w = 1.4f * u
    drawRect(
        color = color,
        topLeft = Offset(2f * u, 2f * u),
        size = Size(14f * u, 14f * u),
        style = Stroke(width = w),
    )
    for (i in 1..3) {
        val p = (2f + 3.5f * i) * u
        drawLine(color = color, start = Offset(p, 2f * u), end = Offset(p, 16f * u), strokeWidth = w)
        drawLine(color = color, start = Offset(2f * u, p), end = Offset(16f * u, p), strokeWidth = w)
    }
}

private fun DrawScope.drawDiamondIcon(color: Color, u: Float) {
    val outer = Path().apply {
        moveTo(9f * u, 1.5f * u)
        lineTo(16.5f * u, 9f * u)
        lineTo(9f * u, 16.5f * u)
        lineTo(1.5f * u, 9f * u)
        close()
    }
    drawPath(path = outer, color = color, style = Stroke(width = 1.4f * u))
    val inner = Path().apply {
        moveTo(9f * u, 5.5f * u)
        lineTo(12.5f * u, 9f * u)
        lineTo(9f * u, 12.5f * u)
        lineTo(5.5f * u, 9f * u)
        close()
    }
    drawPath(path = inner, color = color)
}

private fun DrawScope.drawMenuIcon(color: Color, u: Float) {
    listOf(5f, 9f, 13f).forEach { y ->
        drawLine(
            color = color,
            start = Offset(3f * u, y * u),
            end = Offset(15f * u, y * u),
            strokeWidth = 1.6f * u,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawSquareIcon(color: Color, u: Float) {
    drawRect(
        color = color,
        topLeft = Offset(2f * u, 2f * u),
        size = Size(14f * u, 14f * u),
        style = Stroke(width = 1.4f * u),
    )
    drawRect(
        color = color,
        topLeft = Offset(6f * u, 6f * u),
        size = Size(6f * u, 6f * u),
    )
}

private fun DrawScope.drawGearIcon(color: Color, u: Float) {
    drawCircle(
        color = color,
        radius = 5.2f * u,
        center = center,
        style = Stroke(width = 2.6f * u),
    )
    for (i in 0 until 8) {
        rotate(degrees = 45f * i, pivot = center) {
            drawRect(
                color = color,
                topLeft = Offset(7.5f * u, 0.8f * u),
                size = Size(3f * u, 3.2f * u),
            )
        }
    }
}

private fun DrawScope.drawRefreshIcon(color: Color, u: Float) {
    drawArc(
        color = color,
        startAngle = -40f,
        sweepAngle = 290f,
        useCenter = false,
        topLeft = Offset(3f * u, 3f * u),
        size = Size(12f * u, 12f * u),
        style = Stroke(width = 1.5f * u, cap = StrokeCap.Round),
    )
    val head = Path().apply {
        moveTo(13.6f * u, 1.6f * u)
        lineTo(16.6f * u, 6.0f * u)
        lineTo(10.8f * u, 6.0f * u)
        close()
    }
    drawPath(path = head, color = color)
}

private fun DrawScope.drawBackIcon(color: Color, u: Float) {
    val w = 1.6f * u
    val tip = Offset(3f * u, 9f * u)
    drawLine(color = color, start = tip, end = Offset(15f * u, 9f * u), strokeWidth = w, cap = StrokeCap.Round)
    drawLine(color = color, start = tip, end = Offset(8f * u, 4f * u), strokeWidth = w, cap = StrokeCap.Round)
    drawLine(color = color, start = tip, end = Offset(8f * u, 14f * u), strokeWidth = w, cap = StrokeCap.Round)
}

private fun DrawScope.drawMoreIcon(color: Color, u: Float) {
    listOf(4f, 9f, 14f).forEach { y ->
        drawCircle(color = color, radius = 1.4f * u, center = Offset(9f * u, y * u))
    }
}