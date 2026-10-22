package com.slh.studentlabhub.ui.screens.labdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.slh.studentlabhub.data.local.decodeSampledFile
import com.slh.studentlabhub.ui.components.SlhIcon
import com.slh.studentlabhub.ui.components.SlhIconView
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LabPhotosRow(
    photos: List<String>,
    isAdding: Boolean,
    onAddClick: () -> Unit,
    onDelete: (String) -> Unit,
) {
    var opened by remember { mutableStateOf<String?>(null) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        photos.forEach { path ->
            PhotoThumb(
                path = path,
                size = 76.dp,
                onClick = { opened = path },
            )
        }
        AddPhotoTile(isAdding = isAdding, onClick = onAddClick)
    }

    opened?.let { path ->
        PhotoViewer(
            path = path,
            onDismiss = { opened = null },
            onDelete = {
                onDelete(path)
                opened = null
            },
        )
    }
}

@Composable
private fun rememberPhoto(path: String, maxSide: Int): ImageBitmap? {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, path, maxSide) {
        value = withContext(Dispatchers.IO) {
            runCatching { decodeSampledFile(path, maxSide)?.asImageBitmap() }.getOrNull()
        }
    }
    return bitmap
}

@Composable
private fun PhotoThumb(
    path: String,
    size: Dp,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val bitmap = rememberPhoto(path, maxSide = 256)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(SlhSurface)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Image, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Фото к работе",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size),
            )
        }
    }
}

@Composable
private fun AddPhotoTile(
    isAdding: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .size(76.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(enabled = !isAdding, role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (isAdding) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = SlhAccent,
                strokeWidth = 2.dp,
            )
        } else {
            SlhIconView(icon = SlhIcon.Plus, color = SlhTextMuted, size = 14.dp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "фото",
                color = SlhTextMuted,
                fontFamily = SlhMono,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun PhotoViewer(
    path: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    var confirm by remember { mutableStateOf(false) }
    val bitmap = rememberPhoto(path, maxSide = 1600)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SlhSurface)
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 560.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (bitmap == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = SlhAccent,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Фото к работе",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (confirm) "НАЖМИ ЕЩЁ РАЗ — УДАЛИТЬ" else "УДАЛИТЬ",
                    color = SlhDanger,
                    fontFamily = SlhMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(role = Role.Button) {
                            if (confirm) onDelete() else confirm = true
                        }
                        .padding(8.dp),
                )
                Text(
                    text = "ЗАКРЫТЬ",
                    color = SlhTextMuted,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(role = Role.Button, onClick = onDismiss)
                        .padding(8.dp),
                )
            }
        }
    }
}
