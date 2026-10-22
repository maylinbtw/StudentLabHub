package com.slh.studentlabhub.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.slh.studentlabhub.domain.model.AppSettings
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "slh_reminders"

    fun update(context: Context, settings: AppSettings) {
        val workManager = WorkManager.getInstance(context)
        if (settings.needsBackgroundWork) {
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.HOURS).build()
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        } else {
            workManager.cancelUniqueWork(WORK_NAME)
        }
    }
}
