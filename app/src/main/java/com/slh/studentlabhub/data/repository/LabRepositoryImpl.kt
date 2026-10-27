package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity
import com.slh.studentlabhub.data.local.entity.SubjectWithLabs
import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabDeadline
import com.slh.studentlabhub.domain.model.LabDetails
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

class LabRepositoryImpl(
    private val database: AppDatabase,
) : LabRepository {

    private val subjectDao = database.subjectDao()
    private val labDao = database.labDao()

    override fun observeSubjects(): Flow<List<SubjectSummary>> =
        subjectDao.observeSubjectsWithLabs(archived = false).map { list -> list.map { it.toSummary() } }

    override fun observeArchivedSubjects(): Flow<List<SubjectSummary>> =
        subjectDao.observeSubjectsWithLabs(archived = true).map { list -> list.map { it.toSummary() } }

    override suspend fun setSubjectArchived(subjectId: Long, archived: Boolean) {
        subjectDao.setArchived(subjectId, archived)
    }

    override suspend fun archiveSemester(semester: Int): List<String> =
        database.withTransaction {
            val courseIds = subjectDao.courseIdsInSemester(semester)
            subjectDao.archiveSemester(semester)
            courseIds
        }

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

    override fun observePendingDeadlines(): Flow<List<LabDeadline>> =
        labDao.observePendingDeadlines().map { rows ->
            rows.map { row ->
                LabDeadline(
                    labId = row.lab.id,
                    subjectId = row.lab.subjectId,
                    subjectTitle = row.subjectTitle,
                    type = row.lab.type,
                    number = row.lab.number,
                    title = row.lab.title,
                    deadline = row.lab.deadline,
                    needsRedo = row.lab.needsRedo,
                )
            }
        }

    override suspend fun getLabDetails(labId: Long): LabDetails? {
        val lab = labDao.getById(labId) ?: return null
        val subject = subjectDao.getById(lab.subjectId)
        return LabDetails(
            lab = lab.toDomain(),
            subjectTitle = subject?.title.orEmpty(),
            scheduleTitle = subject?.scheduleTitle,
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

    override suspend fun updateSubject(subjectId: Long, title: String, semester: Int, scheduleTitle: String?) {
        subjectDao.update(id = subjectId, title = title, semester = semester, scheduleTitle = scheduleTitle)
    }

    override suspend fun deleteSubject(subjectId: Long) {
        subjectDao.deleteById(subjectId)
    }

    override suspend fun linkSubjectToCourse(subjectId: Long, courseId: String) {
        database.withTransaction {
            subjectDao.clearCourseLink(courseId)
            subjectDao.setCourseLink(subjectId, courseId)
        }
    }

    override suspend fun unlinkCourse(courseId: String) {
        subjectDao.clearCourseLink(courseId)
    }

    override suspend fun createSubjectForCourse(title: String, courseId: String): Long =
        database.withTransaction {
            subjectDao.clearCourseLink(courseId)
            val semester = subjectDao.maxSemester() ?: 1
            subjectDao.insert(
                SubjectEntity(
                    title = title,
                    semester = semester,
                    classroomCourseId = courseId,
                ),
            )
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

    override suspend fun insertDemoDataIfEmpty(): Boolean =
        database.withTransaction {
            if (subjectDao.count() > 0) return@withTransaction false

            insertSubjectWithWorks(
                title = Strings.pick("ТРПО", "Software Engineering"),
                semester = 4,
                works = listOf(
                    SeedWork(WorkType.LAB, Strings.pick("Анализ задачи", "Problem analysis"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("Требования", "Requirements"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("Функц. требования", "Functional requirements"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("Модель ЖЦ", "Life cycle model"), LabStatus.DONE),
                    SeedWork(WorkType.LAB, Strings.pick("Структурное пр.", "Structured design"), LabStatus.DONE),
                    SeedWork(WorkType.LAB, Strings.pick("UX-прототипы", "UX prototypes"), LabStatus.NOT_DONE),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Диаграмма вариантов использования", "Use case diagram"), LabStatus.DEFENDED, 9),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Диаграмма классов", "Class diagram"), LabStatus.DEFENDED, 8),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Тест-кейсы", "Test cases"), LabStatus.NOT_DONE),
                ),
            )
            insertSubjectWithWorks(
                title = Strings.pick("Операционные системы", "Operating systems"),
                semester = 4,
                works = listOf(
                    SeedWork(WorkType.LAB, Strings.pick("Файловая система Linux", "Linux file system"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("Процессы", "Processes"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("Потоки", "Threads"), LabStatus.DONE),
                    SeedWork(WorkType.LAB, Strings.pick("Синхронизация", "Synchronization"), LabStatus.NOT_DONE),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Команды Linux", "Linux commands"), LabStatus.DEFENDED, 7),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Shell-скрипты", "Shell scripts"), LabStatus.DEFENDED, 8),
                ),
            )
            insertSubjectWithWorks(
                title = Strings.pick("Программирование Java", "Java programming"),
                semester = 4,
                works = listOf(
                    SeedWork(WorkType.LAB, Strings.pick("Основы синтаксиса", "Syntax basics"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("ООП", "OOP"), LabStatus.DEFENDED),
                    SeedWork(WorkType.LAB, Strings.pick("Коллекции", "Collections"), LabStatus.DEFENDED),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Исключения", "Exceptions"), LabStatus.DEFENDED, 9),
                    SeedWork(WorkType.PRACTICE, Strings.pick("Ввод-вывод", "Input/output"), LabStatus.DEFENDED, 9),
                ),
            )
            true
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
        WorkType.LAB -> Strings.get(R.string.default_lab_title)
        WorkType.PRACTICE -> Strings.get(R.string.default_practice_title)
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
        .filter { !it.needsRedo }
        .mapNotNull { it.score }
    return SubjectSummary(
        id = subject.id,
        title = subject.title,
        semester = subject.semester,
        totalLabs = labs.size,
        doneLabs = done,
        defendedLabs = defended,
        averageScore = if (scores.isEmpty()) null else scores.average(),
        classroomCourseId = subject.classroomCourseId,
        archived = subject.archived,
        redoCount = labs.count { it.needsRedo },
        scheduleTitle = subject.scheduleTitle,
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
    link = link,
    needsRedo = needsRedo,
    classroomTaskId = classroomTaskId,
    defendQueued = defendQueued,
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
    classroomTaskId = classroomTaskId,
    link = link,
    needsRedo = needsRedo,
    defendQueued = defendQueued,
)
