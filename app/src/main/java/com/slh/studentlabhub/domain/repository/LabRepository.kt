package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.Lab
import com.slh.studentlabhub.domain.model.LabDetails
import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.model.WorkType
import kotlinx.coroutines.flow.Flow

interface LabRepository {
    fun observeSubjects(): Flow<List<SubjectSummary>>

    fun observeSubjectDetails(subjectId: Long): Flow<SubjectDetails?>

    suspend fun getLabDetails(labId: Long): LabDetails?

    suspend fun addSubject(title: String, semester: Int, labsCount: Int, practiceCount: Int)

    suspend fun deleteSubject(subjectId: Long)

    suspend fun addWork(subjectId: Long, type: WorkType)

    suspend fun updateLab(lab: Lab)

    suspend fun deleteLab(labId: Long)

    suspend fun seedIfEmpty()
}