package com.slh.studentlabhub

import android.app.Application
import com.slh.studentlabhub.di.AppContainer
import kotlinx.coroutines.launch

class SlhApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.applicationScope.launch {
            container.referenceRepository.ensureSeeded()
        }
    }
}
