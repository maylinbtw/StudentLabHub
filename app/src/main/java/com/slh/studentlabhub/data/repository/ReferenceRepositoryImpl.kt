package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.SessionStore
import com.slh.studentlabhub.data.local.entity.RefCategoryEntity
import com.slh.studentlabhub.data.local.entity.RefEntryEntity
import com.slh.studentlabhub.data.reference.ReferenceSeed
import com.slh.studentlabhub.data.reference.TldrParser
import com.slh.studentlabhub.data.remote.TldrApi
import com.slh.studentlabhub.domain.model.RefCategory
import com.slh.studentlabhub.domain.model.RefCategoryIds
import com.slh.studentlabhub.domain.model.RefEntry
import com.slh.studentlabhub.domain.model.RefEntryDraft
import com.slh.studentlabhub.domain.model.RefSource
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.domain.model.ReferenceData
import com.slh.studentlabhub.domain.model.TldrResult
import com.slh.studentlabhub.domain.repository.ReferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.io.IOException
import java.util.UUID

class ReferenceRepositoryImpl(
    private val database: AppDatabase,
    private val session: SessionStore,
    private val tldrApi: TldrApi,
) : ReferenceRepository {

    private val dao = database.referenceDao()

    override fun observeReference(): Flow<ReferenceData> =
        combine(dao.observeCategories(), dao.observeEntries()) { categories, entries ->
            val categoryTitles = categories.associate { it.id to it.title }
            val entriesByCategory = entries.groupBy { it.categoryId }
            ReferenceData(
                categories = categories
                    .map { category ->
                        RefCategory(
                            id = category.id,
                            tech = category.tech,
                            title = category.title,
                            entryTitles = entriesByCategory[category.id].orEmpty().map { it.title },
                        )
                    }
                    .filter { it.entryTitles.isNotEmpty() },
                entries = entries.map { entry ->
                    RefEntry(
                        id = entry.id,
                        tech = entry.tech,
                        categoryId = entry.categoryId,
                        categoryTitle = categoryTitles[entry.categoryId].orEmpty(),
                        title = entry.title,
                        description = entry.description,
                        example = entry.example,
                        source = entry.source.toRefSource(),
                    )
                },
            )
        }

    override suspend fun ensureSeeded() {
        if (session.referenceVersion >= ReferenceSeed.VERSION && dao.countBuiltinEntries() > 0) return

        val categories = mutableListOf<RefCategoryEntity>()
        val entries = mutableListOf<RefEntryEntity>()
        var entryPosition = 0

        ReferenceSeed.categories.forEachIndexed { categoryIndex, category ->
            categories += RefCategoryEntity(
                id = category.id,
                tech = category.tech,
                title = category.title,
                position = categoryIndex,
            )
            category.entries.forEachIndexed { index, entry ->
                entries += RefEntryEntity(
                    id = "${category.id}_$index",
                    categoryId = category.id,
                    tech = category.tech,
                    title = entry.title,
                    description = entry.description,
                    example = entry.example,
                    position = entryPosition++,
                )
            }
        }

        database.withTransaction {
            dao.deleteBuiltinEntries()
            dao.deleteBuiltinCategories()
            dao.insertCategories(categories)
            dao.insertEntries(entries)
        }
        session.referenceVersion = ReferenceSeed.VERSION
    }

    override suspend fun saveCustomEntry(draft: RefEntryDraft) {
        database.withTransaction {
            val categoryId = draft.categoryId ?: ensureCustomCategory(draft.tech)
            val existing = draft.id?.let { dao.getEntry(it) }
            dao.insertEntry(
                RefEntryEntity(
                    id = existing?.id ?: "custom_${UUID.randomUUID()}",
                    categoryId = categoryId,
                    tech = draft.tech,
                    title = draft.title.trim(),
                    description = draft.description.trim(),
                    example = draft.example.trimEnd(),
                    position = existing?.position ?: nextUserPosition(),
                    source = existing?.source ?: RefEntryEntity.SOURCE_CUSTOM,
                ),
            )
        }
    }

    override suspend fun deleteEntry(entryId: String) {
        val entry = dao.getEntry(entryId) ?: return
        if (entry.source != null) {
            dao.deleteEntry(entryId)
        }
    }

    override suspend fun downloadFromTldr(command: String): TldrResult {
        val entryId = "tldr_$command"
        dao.getEntry(entryId)?.let { return TldrResult.Saved(it.title) }

        val page = try {
            tldrApi.fetch(command)
        } catch (error: IOException) {
            return TldrResult.Failed("Не удалось загрузить: нет интернета или сервер недоступен")
        } ?: return TldrResult.NotFound

        val parsed = TldrParser.parse(command, page.markdown)
        val languageNote = if (page.language == "ru") "" else " (англ.)"

        database.withTransaction {
            ensureCategory(
                id = RefCategoryIds.TLDR_LINUX,
                tech = RefTech.LINUX,
                title = "Загружено из tldr-pages",
                position = TLDR_CATEGORY_POSITION,
            )
            dao.insertEntry(
                RefEntryEntity(
                    id = entryId,
                    categoryId = RefCategoryIds.TLDR_LINUX,
                    tech = RefTech.LINUX,
                    title = parsed.title,
                    description = parsed.description + languageNote,
                    example = parsed.example,
                    position = nextUserPosition(),
                    source = RefEntryEntity.SOURCE_TLDR,
                ),
            )
        }
        return TldrResult.Saved(parsed.title)
    }

    override fun observeTldrCount(): Flow<Int> = dao.observeTldrCount()

    override fun observeCustomCount(): Flow<Int> = dao.observeCustomCount()

    override suspend fun clearTldrDownloads() {
        dao.deleteTldrEntries()
    }

    private suspend fun ensureCustomCategory(tech: RefTech): String {
        val id = RefCategoryIds.custom(tech)
        ensureCategory(
            id = id,
            tech = tech,
            title = "Мои заметки",
            position = CUSTOM_CATEGORY_POSITION,
        )
        return id
    }

    private suspend fun ensureCategory(id: String, tech: RefTech, title: String, position: Int) {
        if (dao.getCategory(id) == null) {
            dao.insertCategory(
                RefCategoryEntity(
                    id = id,
                    tech = tech,
                    title = title,
                    position = position,
                ),
            )
        }
    }

    private fun nextUserPosition(): Int = (System.currentTimeMillis() / 1000L).toInt()

    private companion object {
        const val CUSTOM_CATEGORY_POSITION = 10_000
        const val TLDR_CATEGORY_POSITION = 10_001
    }
}

private fun String?.toRefSource(): RefSource = when (this) {
    RefEntryEntity.SOURCE_CUSTOM -> RefSource.CUSTOM
    RefEntryEntity.SOURCE_TLDR -> RefSource.TLDR
    else -> RefSource.BUILTIN
}
