package com.slh.studentlabhub.di

import android.content.Context
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.repository.LabRepositoryImpl
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: AppDatabase by lazy { AppDatabase.create(appContext) }

    val labRepository: LabRepository by lazy { LabRepositoryImpl(database) }
}