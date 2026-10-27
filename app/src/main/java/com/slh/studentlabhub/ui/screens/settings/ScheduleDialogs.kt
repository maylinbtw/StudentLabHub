package com.slh.studentlabhub.ui.screens.settings

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slh.studentlabhub.R
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.i18n.Strings
import com.slh.studentlabhub.ui.components.SlhDialog
import com.slh.studentlabhub.ui.components.SlhDialogOutlinedButton
import com.slh.studentlabhub.ui.components.SlhDialogPrimaryButton
import com.slh.studentlabhub.ui.components.SlhLabeledField
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhContent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

@Composable
fun GroupPickerDialog(
    groups: List<String>,
    selected: String,
    isLoading: Boolean,
    errorMessage: String?,
    onPick: (String) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    SlhDialog(
        overline = Strings.get(R.string.schedule_banner_overline),
        title = Strings.get(R.string.schedule_pick_group_title),
        onDismissRequest = onDismiss,
    ) {
        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = SlhAccent,
                    strokeWidth = 2.dp,
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = SlhDanger,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(14.dp))
                SlhDialogPrimaryButton(
                    text = Strings.get(R.string.action_retry),
                    enabled = true,
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            groups.isEmpty() -> Text(
                text = Strings.get(R.string.schedule_no_groups),
                color = SlhTextMuted,
                style = MaterialTheme.typography.bodyMedium,
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                groups.forEach { group ->
                    GroupOption(
                        text = group,
                        selected = group == selected,
                        onClick = { onPick(group) },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        SlhDialogOutlinedButton(
            text = Strings.get(R.string.action_cancel),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun GroupOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, if (selected) SlhAccent else SlhOutline, shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .border(1.5.dp, if (selected) SlhAccent else SlhTextMuted, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SlhAccent),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = if (selected) SlhContent else SlhTextSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun ScheduleUrlDialog(
    initialUrl: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var url by rememberSaveable { mutableStateOf(initialUrl) }
    val isValid = url.trim().let { it.startsWith("http://") || it.startsWith("https://") }
    SlhDialog(
        overline = Strings.get(R.string.schedule_banner_overline),
        title = Strings.get(R.string.schedule_url_title),
        onDismissRequest = onDismiss,
    ) {
        SlhLabeledField(
            label = Strings.get(R.string.schedule_url_field),
            value = url,
            onValueChange = { url = it.take(300) },
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = Strings.get(R.string.schedule_url_reset),
            color = SlhAccent,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(role = Role.Button) { url = AppSettings.DEFAULT_SCHEDULE_URL }
                .padding(vertical = 4.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SlhDialogOutlinedButton(
                text = Strings.get(R.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
            SlhDialogPrimaryButton(
                text = Strings.get(R.string.action_save),
                enabled = isValid,
                onClick = { onConfirm(url.trim()) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 560)
@Composable
private fun GroupPickerDialogPreview() {
    StudentLabHubTheme {
        GroupPickerDialog(
            groups = listOf("ПЗТ-47", "ПЗТ-46", "ПЗТ-45", "ПЗТ-44", "ПЗТ-42"),
            selected = "ПЗТ-44",
            isLoading = false,
            errorMessage = null,
            onPick = {},
            onRetry = {},
            onDismiss = {},
        )
    }
}
