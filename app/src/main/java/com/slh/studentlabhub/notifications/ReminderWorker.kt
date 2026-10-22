package com.slh.studentlabhub.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.data.remote.AuthState
import com.slh.studentlabhub.widget.DeadlinesWidget
import kotlinx.coroutines.CancellationException

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as SlhApplication).container
        val settings = container.settingsRepository.settings.value
        val classroom = container.classroomRepository

        if (settings.backgroundSync && classroom.signedInEmail != null) {
            val lastSync = classroom.lastSyncAt ?: 0L
            if (System.currentTimeMillis() - lastSync >= SYNC_INTERVAL_MS) {
                try {
                    val auth = container.googleAuthManager.authorize()
                    if (auth is AuthState.Authorized) {
                        classroom.sync(auth.accessToken)
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (ignored: Exception) {
                }
            }
        }

        if (settings.remindersEnabled || settings.redoAlerts) {
            ReminderChecker(applicationContext, container).run(settings)
        }
        DeadlinesWidget.refresh(applicationContext)
        return Result.success()
    }

    private companion object {
        const val SYNC_INTERVAL_MS = 6L * 60 * 60 * 1000
    }
}
