package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.SubjectDetails
import com.slh.studentlabhub.domain.model.SubjectSummary
import kotlinx.coroutines.flow.Flow

interface LabRepository {
    fun observeSubjects(): Flow<List<SubjectSummary>>

    fun observeSubjectDetails(subjectId: Long): Flow<SubjectDetails?>

    suspend fun addSubject(title: String, semester: Int, labsCount: Int)

    suspend fun deleteSubject(subjectId: Long)

    suspend fun seedIfEmpty()
}