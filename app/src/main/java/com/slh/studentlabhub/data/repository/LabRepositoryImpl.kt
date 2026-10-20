package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity
import com.slh.studentlabhub.data.local.entity.SubjectWithLabs
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LabRepositoryImpl(
    private val database: AppDatabase,
) : LabRepository {

    private val subjectDao = database.subjectDao()
    private val labDao = database.labDao()

    override fun observeSubjects(): Flow<List<SubjectSummary>> =
        subjectDao.observeSubjectsWithLabs().map { list -> list.map { it.toSummary() } }

    override suspend fun addSubject(title: String, semester: Int, labsCount: Int) {
        database.withTransaction {
            val subjectId = subjectDao.insert(SubjectEntity(title = title, semester = semester))
            if (labsCount > 0) {
                labDao.insertAll(
                    (1..labsCount).map { number ->
                        LabEntity(
                            subjectId = subjectId,
                            number = number,
                            title = "Лабораторная работа",
                        )
                    },
                )
            }
        }
    }

    override suspend fun seedIfEmpty() {
        database.withTransaction {
            if (subjectDao.count() > 0) return@withTransaction

            insertSubjectWithLabs(
                title = "ТРПО",
                semester = 4,
                labs = listOf(
                    SeedLab("Анализ задачи", LabStatus.DEFENDED, 9),
                    SeedLab("Требования", LabStatus.DEFENDED, 8),
                    SeedLab("Функц. требования", LabStatus.DEFENDED, 8),
                    SeedLab("Модель ЖЦ", LabStatus.DONE, 9),
                    SeedLab("Структурное пр.", LabStatus.DONE, null),
                    SeedLab("UX-прототипы", LabStatus.NOT_DONE, null),
                    SeedLab("UI-прототипы", LabStatus.NOT_DONE, null),
                    SeedLab("Тестирование", LabStatus.NOT_DONE, null),
                ),
            )
            insertSubjectWithLabs(
                title = "Операционные системы",
                semester = 4,
                labs = listOf(
                    SeedLab("Файловая система Linux", LabStatus.DEFENDED, 7),
                    SeedLab("Процессы", LabStatus.DEFENDED, 8),
                    SeedLab("Потоки", LabStatus.DONE, null),
                    SeedLab("Синхронизация", LabStatus.NOT_DONE, null),
                    SeedLab("Управление памятью", LabStatus.NOT_DONE, null),
                    SeedLab("Shell-скрипты", LabStatus.NOT_DONE, null),
                ),
            )
            insertSubjectWithLabs(
                title = "Программирование Java",
                semester = 4,
                labs = listOf(
                    SeedLab("Основы синтаксиса", LabStatus.DEFENDED, 9),
                    SeedLab("ООП", LabStatus.DEFENDED, 9),
                    SeedLab("Коллекции", LabStatus.DEFENDED, 10),
                    SeedLab("Исключения", LabStatus.DEFENDED, 8),
                    SeedLab("Ввод-вывод", LabStatus.DEFENDED, 9),
                ),
            )
        }
    }

    private suspend fun insertSubjectWithLabs(
        title: String,
        semester: Int,
        labs: List<SeedLab>,
    ) {
        val subjectId = subjectDao.insert(SubjectEntity(title = title, semester = semester))
        labDao.insertAll(
            labs.mapIndexed { index, seed ->
                LabEntity(
                    subjectId = subjectId,
                    number = index + 1,
                    title = seed.title,
                    status = seed.status,
                    score = seed.score,
                )
            },
        )
    }

    private data class SeedLab(
        val title: String,
        val status: LabStatus,
        val score: Int?,
    )
}

private fun SubjectWithLabs.toSummary(): SubjectSummary {
    val done = labs.count { it.status != LabStatus.NOT_DONE }
    val defended = labs.count { it.status == LabStatus.DEFENDED }
    val scores = labs
        .filter { it.status == LabStatus.DEFENDED }
        .mapNotNull { it.score }
    return SubjectSummary(
        id = subject.id,
        title = subject.title,
        semester = subject.semester,
        totalLabs = labs.size,
        doneLabs = done,
        defendedLabs = defended,
        averageScore = if (scores.isEmpty()) null else scores.average(),
    )
}