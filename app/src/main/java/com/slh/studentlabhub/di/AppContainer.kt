package com.slh.studentlabhub.di

import android.content.Context
import com.slh.studentlabhub.data.backup.BackupManager
import com.slh.studentlabhub.data.classroom.ClassroomMerger
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.LabPhotoStore
import com.slh.studentlabhub.data.local.ReminderStore
import com.slh.studentlabhub.data.local.SessionStore
import com.slh.studentlabhub.data.remote.ClassroomApi
import com.slh.studentlabhub.data.remote.GoogleAuthManager
import com.slh.studentlabhub.data.remote.TldrApi
import com.slh.studentlabhub.data.repository.ClassroomRepositoryImpl
import com.slh.studentlabhub.data.repository.LabRepositoryImpl
import com.slh.studentlabhub.data.repository.ReferenceRepositoryImpl
import com.slh.studentlabhub.data.repository.SettingsRepositoryImpl
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.LabRepository
import com.slh.studentlabhub.domain.repository.ReferenceRepository
import com.slh.studentlabhub.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.slh.studentlabhub.data.remote.ScheduleApi
import com.slh.studentlabhub.data.repository.ScheduleRepositoryImpl
import com.slh.studentlabhub.domain.repository.ScheduleRepository
import com.slh.studentlabhub.data.repository.ArticleRepositoryImpl
import com.slh.studentlabhub.domain.repository.ArticleRepository
import com.slh.studentlabhub.data.repository.ReadingRepositoryImpl
import com.slh.studentlabhub.domain.repository.ReadingRepository

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: AppDatabase by lazy { AppDatabase.create(appContext) }

    private val sessionStore: SessionStore by lazy { SessionStore(appContext) }

    private val classroomApi: ClassroomApi by lazy { ClassroomApi() }

    val googleAuthManager: GoogleAuthManager by lazy { GoogleAuthManager(appContext) }

    val reminderStore: ReminderStore by lazy { ReminderStore(appContext) }

    val labPhotoStore: LabPhotoStore by lazy { LabPhotoStore(appContext) }

    val backupManager: BackupManager by lazy {
        BackupManager(
            database = database,
            settingsRepository = settingsRepository,
            classroomRepository = classroomRepository,
            readingRepository = readingRepository,
            archivedCourseIds = { sessionStore.archivedCourseIds },
        )
    }

    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(appContext) }

    val labRepository: LabRepository by lazy { LabRepositoryImpl(database) }

    val referenceRepository: ReferenceRepository by lazy {
        ReferenceRepositoryImpl(
            database = database,
            session = sessionStore,
            tldrApi = TldrApi(),
        )
    }

    val articleRepository: ArticleRepository by lazy { ArticleRepositoryImpl(appContext.assets) }

    val readingRepository: ReadingRepository by lazy { ReadingRepositoryImpl(appContext) }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(
            database = database,
            api = ScheduleApi(),
            session = sessionStore,
            settingsRepository = settingsRepository,
        )
    }

    val classroomRepository: ClassroomRepository by lazy {
        ClassroomRepositoryImpl(
            database = database,
            api = classroomApi,
            session = sessionStore,
            authManager = googleAuthManager,
            merger = ClassroomMerger(database),
            scope = applicationScope,
        )
    }
}
