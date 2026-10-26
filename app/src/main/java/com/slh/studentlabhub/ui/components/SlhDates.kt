package com.slh.studentlabhub.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.slh.studentlabhub.ui.theme.SlhAccent
import com.slh.studentlabhub.ui.theme.SlhDanger
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

private val dayMonthFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("d MMMM, EEE", Locale.getDefault())
private val shortFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("dd.MM", Locale.getDefault())

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun Long.formatDayMonth(): String = toLocalDate().format(dayMonthFormatter)

fun Long.formatShortDate(): String = toLocalDate().format(shortFormatter)

fun daysUntil(dueAt: Long, now: Long): Long =
    ChronoUnit.DAYS.between(now.toLocalDate(), dueAt.toLocalDate())

fun relativeDueLabel(dueAt: Long?, now: Long): String {
    if (dueAt == null) return Strings.get(R.string.due_none)
    val days = daysUntil(dueAt, now)
    return when {
        dueAt < now && days <= 0L -> if (days == 0L) Strings.get(R.string.due_today_overdue) else Strings.get(R.string.due_overdue_days, (-days).toInt())
        days == 0L -> Strings.get(R.string.due_today)
        days == 1L -> Strings.get(R.string.due_tomorrow)
        days in 2L..6L -> Strings.get(R.string.due_in_days, days.toInt())
        else -> dueAt.formatShortDate()
    }
}

private fun LocalDate.toUtcMillis(): Long =
    atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

private fun utcMillisToLocalEndOfDay(utcMillis: Long): Long =
    Instant.ofEpochMilli(utcMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .atTime(23, 59)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinePickerDialog(
    initial: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    onClear: (() -> Unit)? = null,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = (initial?.toLocalDate() ?: LocalDate.now()).toUtcMillis(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = {
                    state.selectedDateMillis?.let { onConfirm(utcMillisToLocalEndOfDay(it)) }
                },
            ) {
                Text(text = Strings.get(R.string.action_done), color = SlhAccent, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            Row {
                if (onClear != null) {
                    TextButton(onClick = onClear) {
                        Text(text = Strings.get(R.string.action_remove), color = SlhDanger, style = MaterialTheme.typography.labelLarge)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(text = Strings.get(R.string.action_cancel), color = SlhTextMuted, style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        colors = DatePickerDefaults.colors(containerColor = SlhSurface),
    ) {
        DatePicker(
            state = state,
            colors = DatePickerDefaults.colors(containerColor = SlhSurface),
        )
    }
}
