package com.slh.studentlabhub.domain.repository

import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.ReferenceData
import com.slh.studentlabhub.domain.model.TldrResult
import kotlinx.coroutines.flow.Flow

interface ReferenceRepository {
    fun observeReference(): Flow<ReferenceData>

    suspend fun ensureSeeded()

    suspend fun saveCustomEntry(draft: RefEntryDraft)

    suspend fun deleteEntry(entryId: String)

    suspend fun downloadFromTldr(command: String): TldrResult

    fun observeTldrCount(): Flow<Int>

    fun observeCustomCount(): Flow<Int>

    suspend fun clearTldrDownloads()
}
