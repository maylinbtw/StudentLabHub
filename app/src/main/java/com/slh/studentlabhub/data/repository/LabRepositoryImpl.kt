package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity
import com.slh.studentlabhub.data.local.entity.SubjectWithLabs
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabDetails
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.WorkType
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

    override fun observeSubjectDetails(subjectId: Long): Flow<SubjectDetails?> =
        subjectDao.observeSubjectWithLabs(subjectId).map { subjectWithLabs ->
            subjectWithLabs?.let {
                SubjectDetails(
                    summary = it.toSummary(),
                    labs = it.labs
                        .sortedWith(compareBy<LabEntity>({ lab -> lab.type.ordinal }, { lab -> lab.number }))
                        .map { lab -> lab.toDomain() },
                )
            }
        }

    override suspend fun getLabDetails(labId: Long): LabDetails? {
        val lab = labDao.getById(labId) ?: return null
        val subjectTitle = subjectDao.getTitle(lab.subjectId).orEmpty()
        return LabDetails(
            lab = lab.toDomain(),
            subjectTitle = subjectTitle,
        )
    }

    override suspend fun addSubject(
        title: String,
        semester: Int,
        labsCount: Int,
        practiceCount: Int,
    ) {
        database.withTransaction {
            val subjectId = subjectDao.insert(SubjectEntity(title = title, semester = semester))
            val labs = (1..labsCount).map { number ->
                LabEntity(
                    subjectId = subjectId,
                    type = WorkType.LAB,
                    number = number,
                    title = defaultTitle(WorkType.LAB),
                )
            }
            val practices = (1..practiceCount).map { number ->
                LabEntity(
                    subjectId = subjectId,
                    type = WorkType.PRACTICE,
                    number = number,
                    title = defaultTitle(WorkType.PRACTICE),
                )
            }
            val all = labs + practices
            if (all.isNotEmpty()) {
                labDao.insertAll(all)
            }
        }
    }

    override suspend fun updateSubject(subjectId: Long, title: String, semester: Int) {
        subjectDao.update(id = subjectId, title = title, semester = semester)
    }

    override suspend fun deleteSubject(subjectId: Long) {
        subjectDao.deleteById(subjectId)
    }

    override suspend fun addWork(subjectId: Long, type: WorkType) {
        database.withTransaction {
            val nextNumber = (labDao.maxNumber(subjectId, type.name) ?: 0) + 1
            labDao.insert(
                LabEntity(
                    subjectId = subjectId,
                    type = type,
                    number = nextNumber,
                    title = defaultTitle(type),
                ),
            )
        }
    }

    override suspend fun updateLab(lab: Lab) {
        labDao.update(lab.toEntity())
    }

    override suspend fun deleteLab(labId: Long) {
        labDao.deleteById(labId)
    }

    override suspend fun seedIfEmpty() {
        database.withTransaction {
            if (subjectDao.count() > 0) return@withTransaction

            insertSubjectWithWorks(
                title = "ТРПО",
                semester = 4,
                works = listOf(
                    SeedWork(WorkType.LAB, "Анализ задачи", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "Требования", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "Функц. требования", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "Модель ЖЦ", LabStatus.DONE),
                    SeedWork(WorkType.LAB, "Структурное пр.", LabStatus.DONE),
                    SeedWork(WorkType.LAB, "UX-прототипы", LabStatus.NOT_DONE),
                    SeedWork(WorkType.PRACTICE, "Диаграмма вариантов использования", LabStatus.DEFENDED, 9),
                    SeedWork(WorkType.PRACTICE, "Диаграмма классов", LabStatus.DEFENDED, 8),
                    SeedWork(WorkType.PRACTICE, "Тест-кейсы", LabStatus.NOT_DONE),
                ),
            )
            insertSubjectWithWorks(
                title = "Операционные системы",
                semester = 4,
                works = listOf(
                    SeedWork(WorkType.LAB, "Файловая система Linux", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "Процессы", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "Потоки", LabStatus.DONE),
                    SeedWork(WorkType.LAB, "Синхронизация", LabStatus.NOT_DONE),
                    SeedWork(WorkType.PRACTICE, "Команды Linux", LabStatus.DEFENDED, 7),
                    SeedWork(WorkType.PRACTICE, "Shell-скрипты", LabStatus.DEFENDED, 8),
                ),
            )
            insertSubjectWithWorks(
                title = "Программирование Java",
                semester = 4,
                works = listOf(
                    SeedWork(WorkType.LAB, "Основы синтаксиса", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "ООП", LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, "Коллекции", LabStatus.DEFENDED),
                    SeedWork(WorkType.PRACTICE, "Исключения", LabStatus.DEFENDED, 9),
                    SeedWork(WorkType.PRACTICE, "Ввод-вывод", LabStatus.DEFENDED, 9),
                ),
            )
        }
    }

    private suspend fun insertSubjectWithWorks(
        title: String,
        semester: Int,
        works: List<SeedWork>,
    ) {
        val subjectId = subjectDao.insert(SubjectEntity(title = title, semester = semester))
        var labNumber = 0
        var practiceNumber = 0
        labDao.insertAll(
            works.map { seed ->
                val number = when (seed.type) {
                    WorkType.LAB -> ++labNumber
                    WorkType.PRACTICE -> ++practiceNumber
                }
                LabEntity(
                    subjectId = subjectId,
                    type = seed.type,
                    number = number,
                    title = seed.title,
                    status = seed.status,
                    score = seed.score,
                )
            },
        )
    }

    private fun defaultTitle(type: WorkType): String = when (type) {
        WorkType.LAB -> "Лабораторная работа"
        WorkType.PRACTICE -> "Практическая работа"
    }

    private data class SeedWork(
        val type: WorkType,
        val title: String,
        val status: LabStatus,
        val score: Int? = null,
    )
}

private fun SubjectWithLabs.toSummary(): SubjectSummary {
    val done = labs.count { it.status != LabStatus.NOT_DONE }
    val defended = labs.count { it.status == LabStatus.DEFENDED }
    val scores = labs
        .filter { it.type == WorkType.PRACTICE }
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

private fun LabEntity.toDomain(): Lab = Lab(
    id = id,
    subjectId = subjectId,
    type = type,
    number = number,
    title = title,
    status = status,
    score = score,
    notes = notes,
    deadline = deadline,
)

private fun Lab.toEntity(): LabEntity = LabEntity(
    id = id,
    subjectId = subjectId,
    type = type,
    number = number,
    title = title,
    status = status,
    score = score,
    notes = notes,
    deadline = deadline,
)