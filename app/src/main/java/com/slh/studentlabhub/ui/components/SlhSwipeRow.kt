package com.slh.studentlabhub.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhSurfaceVariant
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SlhSwipeRow(
    forwardLabel: String?,
    backwardLabel: String?,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val threshold = with(density) { 84.dp.toPx() }
    val maxOffset = with(density) { 140.dp.toPx() }
    val offset = remember { Animatable(0f) }
    var armed by remember { mutableStateOf(false) }
    val latestForward by rememberUpdatedState(onForward)
    val latestBackward by rememberUpdatedState(onBackward)
    val canForward = forwardLabel != null
    val canBackward = backwardLabel != null

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(if (offset.value != 0f) SlhSurfaceVariant else SlhBackground)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (offset.value > 0f && forwardLabel != null) {
                Text(
                    text = "$forwardLabel →",
                    color = if (armed) SlhAccent else SlhTextSecondary,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (offset.value < 0f && backwardLabel != null) {
                Text(
                    text = "← $backwardLabel",
                    color = if (armed) SlhAccent else SlhTextMuted,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .background(SlhBackground)
                .pointerInput(canForward, canBackward) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val value = offset.value
                            when {
                                value >= threshold && canForward -> latestForward()
                                value <= -threshold && canBackward -> latestBackward()
                            }
                            armed = false
                            scope.launch { offset.animateTo(0f) }
                        },
                        onDragCancel = {
                            armed = false
                            scope.launch { offset.animateTo(0f) }
                        },
                    ) { change, dragAmount ->
                        change.consume()
                        val min = if (canBackward) -maxOffset else 0f
                        val max = if (canForward) maxOffset else 0f
                        val next = (offset.value + dragAmount).coerceIn(min, max)
                        scope.launch { offset.snapTo(next) }
                        val nowArmed = abs(next) >= threshold
                        if (nowArmed != armed) {
                            armed = nowArmed
                            if (nowArmed) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                },
        ) {
            content()
        }
    }
}
