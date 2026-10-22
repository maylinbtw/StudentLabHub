package com.slh.studentlabhub.di

import android.content.Context
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.SessionStore
import com.slh.studentlabhub.data.remote.ClassroomApi
import com.slh.studentlabhub.data.remote.GoogleAuthManager
import com.slh.studentlabhub.data.repository.ClassroomRepositoryImpl
import com.slh.studentlabhub.data.repository.LabRepositoryImpl
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: AppDatabase by lazy { AppDatabase.create(appContext) }

    private val sessionStore: SessionStore by lazy { SessionStore(appContext) }

    private val classroomApi: ClassroomApi by lazy { ClassroomApi() }

    val googleAuthManager: GoogleAuthManager by lazy { GoogleAuthManager(appContext) }

    val labRepository: LabRepository by lazy { LabRepositoryImpl(database) }

    val classroomRepository: ClassroomRepository by lazy {
        ClassroomRepositoryImpl(
            database = database,
            api = classroomApi,
            session = sessionStore,
            authManager = googleAuthManager,
        )
    }
}