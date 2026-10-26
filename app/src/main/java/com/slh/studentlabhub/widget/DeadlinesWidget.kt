package com.slh.studentlabhub.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import com.slh.studentlabhub.MainActivity
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.notifications.Reminder
import com.slh.studentlabhub.notifications.collectReminders
import com.slh.studentlabhub.ui.components.daysUntil
import com.slh.studentlabhub.ui.components.relativeDueLabel
import com.slh.studentlabhub.ui.theme.colorFor

private const val MAX_ITEMS = 4

class DeadlinesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val container = (context.applicationContext as SlhApplication).container
        val now = System.currentTimeMillis()
        val reminders = runCatching { container.collectReminders() }.getOrDefault(emptyList())
        val upcoming = reminders
            .filter { !it.isRedo && (it.dueAt ?: 0L) >= now }
            .sortedBy { it.dueAt }
        val redoCount = reminders.count { it.isRedo }
        val accent = container.settingsRepository.settings.value.accent.colorFor(dark = true)

        provideContent {
            WidgetContent(
                upcoming = upcoming,
                redoCount = redoCount,
                now = now,
                accent = accent,
            )
        }
    }

    companion object {
        suspend fun refresh(context: Context) {
            runCatching { DeadlinesWidget().updateAll(context) }
        }
    }
}

class DeadlinesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DeadlinesWidget()
}

@Composable
private fun WidgetContent(
    upcoming: List<Reminder>,
    redoCount: Int,
    now: Long,
    accent: Color,
) {
    val context = LocalContext.current
    val white = fixedColor(Color(0xFFFFFFFF))
    val muted = fixedColor(Color(0xFF8A8A8A))
    val accentColor = fixedColor(accent)
    val openApp = Intent(context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .cornerRadius(20.dp)
            .padding(14.dp)
            .clickable(actionStartActivity(openApp)),
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "●●●",
                style = TextStyle(color = accentColor, fontSize = 9.sp),
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = "ДЕДЛАЙНЫ",
                style = TextStyle(
                    color = muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                ),
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                text = upcoming.size.toString(),
                style = TextStyle(
                    color = accentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                ),
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))

        if (redoCount > 0) {
            Text(
                text = "$redoCount на доработку",
                style = TextStyle(color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = GlanceModifier.height(6.dp))
        }

        if (upcoming.isEmpty()) {
            Text(
                text = "Дедлайнов нет — можно выдохнуть",
                style = TextStyle(color = muted, fontSize = 12.sp),
            )
        }

        upcoming.take(MAX_ITEMS).forEach { item ->
            val due = item.dueAt
            val soon = due != null && daysUntil(due, now) <= 2L
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = item.title,
                        style = TextStyle(
                            color = white,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = item.subjectTitle,
                        style = TextStyle(color = muted, fontSize = 11.sp),
                        maxLines = 1,
                    )
                }
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = relativeDueLabel(due, now),
                    style = TextStyle(
                        color = if (soon) accentColor else muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                    ),
                )
            }
            Spacer(modifier = GlanceModifier.height(6.dp))
        }
    }
}

private fun fixedColor(color: Color) = ColorProvider(day = color, night = color)
