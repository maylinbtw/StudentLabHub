package com.slh.studentlabhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.ui.theme.SlhDotInactive
import com.slh.studentlabhub.ui.theme.SlhDotShape
import com.slh.studentlabhub.ui.theme.SlhWhite

@Composable
fun SlhProgressDots(
    total: Int,
    filled: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        repeat(total.coerceAtMost(30)) { index ->
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(SlhDotShape)
                    .background(if (index < filled) SlhWhite else SlhDotInactive),
            )
        }
    }
}