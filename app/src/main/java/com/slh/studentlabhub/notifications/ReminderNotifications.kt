package com.slh.studentlabhub.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.slh.studentlabhub.MainActivity
import com.slh.studentlabhub.R
import kotlin.math.ceil

data class Reminder(
    val key: String,
    val title: String,
    val subjectTitle: String,
    val dueAt: Long?,
    val isRedo: Boolean,
)

object ReminderNotifications {
    private const val CHANNEL_DEADLINES = "deadlines"
    private const val CHANNEL_REDO = "redo"
    private const val ACCENT = 0xFFFF3B30.toInt()

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_DEADLINES,
                    "Дедлайны",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply { description = "Напоминания о сроках сдачи работ" },
                NotificationChannel(
                    CHANNEL_REDO,
                    "На доработку",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = "Когда работу возвращают на доработку" },
            ),
        )
    }

    fun canPost(context: Context): Boolean {
        val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        val permitted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return enabled && permitted
    }

    fun postDeadline(context: Context, reminder: Reminder, millisLeft: Long) {
        post(
            context = context,
            channel = CHANNEL_DEADLINES,
            reminder = reminder,
            title = reminder.title,
            text = "${reminder.subjectTitle} · ${leftText(millisLeft)}",
        )
    }

    fun postRedo(context: Context, reminder: Reminder) {
        post(
            context = context,
            channel = CHANNEL_REDO,
            reminder = reminder,
            title = "На доработку: ${reminder.title}",
            text = "${reminder.subjectTitle} · преподаватель вернул работу",
        )
    }

    @SuppressLint("MissingPermission")
    private fun post(
        context: Context,
        channel: String,
        reminder: Reminder,
        title: String,
        text: String,
    ) {
        if (!canPost(context)) return
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_stat_slh)
            .setColor(ACCENT)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(reminder.key.hashCode(), notification)
        }
    }

    private fun leftText(millisLeft: Long): String {
        val hours = ceil(millisLeft / 3_600_000.0).toLong()
        return when {
            hours <= 1L -> "сдать меньше чем через час"
            hours < 24L -> "сдать через $hours ч"
            else -> "сдать завтра"
        }
    }
}
