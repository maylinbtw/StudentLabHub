package com.slh.studentlabhub

import android.app.Application
import com.slh.studentlabhub.di.AppContainer
import com.slh.studentlabhub.notifications.ReminderNotifications
import com.slh.studentlabhub.notifications.ReminderScheduler
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

class SlhApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ReminderNotifications.createChannels(this)
        container.applicationScope.launch {
            container.referenceRepository.ensureSeeded()
            runCatching { container.classroomRepository.mergeIntoSubjects() }
        }
        container.applicationScope.launch {
            container.settingsRepository.settings
                .distinctUntilChangedBy { it.needsBackgroundWork }
                .collect { settings -> ReminderScheduler.update(this@SlhApplication, settings) }
        }
    }
}
