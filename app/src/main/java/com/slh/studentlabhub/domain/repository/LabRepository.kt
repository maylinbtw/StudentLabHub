package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabDeadline
import com.slh.studentlabhub.domain.model.LabDetails
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.WorkType
import kotlinx.coroutines.flow.Flow

interface LabRepository {
    fun observeSubjects(): Flow<List<SubjectSummary>>

    fun observeArchivedSubjects(): Flow<List<SubjectSummary>>

    suspend fun setSubjectArchived(subjectId: Long, archived: Boolean)

    suspend fun archiveSemester(semester: Int): List<String>

    fun observeSubjectDetails(subjectId: Long): Flow<SubjectDetails?>

    fun observePendingDeadlines(): Flow<List<LabDeadline>>

    suspend fun getLabDetails(labId: Long): LabDetails?

    suspend fun addSubject(title: String, semester: Int, labsCount: Int, practiceCount: Int)

    suspend fun updateSubject(subjectId: Long, title: String, semester: Int, scheduleTitle: String?)

    suspend fun deleteSubject(subjectId: Long)

    suspend fun linkSubjectToCourse(subjectId: Long, courseId: String)

    suspend fun unlinkCourse(courseId: String)

    suspend fun createSubjectForCourse(title: String, courseId: String): Long

    suspend fun addWork(subjectId: Long, type: WorkType)

    suspend fun updateLab(lab: Lab)

    suspend fun deleteLab(labId: Long)

    suspend fun insertDemoDataIfEmpty(): Boolean
}
