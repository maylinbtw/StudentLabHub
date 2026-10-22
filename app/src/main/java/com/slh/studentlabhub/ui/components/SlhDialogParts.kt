package com.slh.studentlabhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.slh.studentlabhub.ui.theme.SlhBlack
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhRed
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhWhite

@Composable
fun SlhDialog(
    overline: String,
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(SlhSurface)
                .border(1.dp, SlhOutline, shape)
                .padding(20.dp),
        ) {
            Text(
                text = overline,
                color = SlhRed,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = SlhWhite,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun SlhLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = modifier) {
        SectionLabel(text = label)
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            interactionSource = interactionSource,
            textStyle = TextStyle(
                color = SlhWhite,
                fontFamily = SlhMono,
                fontSize = 14.sp,
            ),
            cursorBrush = SolidColor(SlhRed),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(SlhBlack)
                .border(1.dp, if (isFocused) SlhRed else SlhOutline, shape)
                .padding(horizontal = 12.dp, vertical = 12.dp),
        )
    }
}

@Composable
fun SlhDialogOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .border(1.dp, SlhOutline, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SlhTextMuted,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun SlhDialogPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        topStart = 2.dp,
        bottomStart = 2.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp,
    )
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(if (enabled) SlhWhite else SlhOutline)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(if (enabled) SlhRed else SlhTextDisabled),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = if (enabled) SlhBlack else SlhTextMuted,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}