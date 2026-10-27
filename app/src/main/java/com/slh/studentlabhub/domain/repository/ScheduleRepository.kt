package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.Lesson
import com.slh.studentlabhub.domain.model.QueuedWork
import com.slh.studentlabhub.domain.model.ScheduleRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ScheduleRepository {
    /** Пары начиная с сегодняшнего дня. */
    fun observeUpcomingLessons(): Flow<List<Lesson>>

    fun observeLessonSubjects(): Flow<List<String>>

    fun observeQueuedWorks(): Flow<List<QueuedWork>>

    val lastSyncAt: StateFlow<Long?>

    val isConfigured: Boolean

    /**
     * Скачивает расписание и сохраняет пары выбранной группы.
     * @param silent не считать изменения (первая загрузка, смена группы).
     */
    suspend fun refresh(silent: Boolean = false): ScheduleRefreshResult

    /** Список групп со страницы — для выбора в настройках. */
    suspend fun fetchGroups(url: String): List<String>

    suspend fun clear()

    suspend fun setDefendQueued(labId: Long, queued: Boolean)
}
