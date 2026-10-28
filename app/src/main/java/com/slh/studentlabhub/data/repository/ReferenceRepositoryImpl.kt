package com.slh.studentlabhub.data.repository

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.SessionStore
import com.slh.studentlabhub.data.local.entity.RefCategoryEntity
import com.slh.studentlabhub.data.local.entity.RefEntryEntity
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
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

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
        val language = when {
            Strings.isBelarusian() -> "be"
            Strings.isRussian() -> "ru"
            else -> "en"
        }
        database.withTransaction {
            moveOrphanedCustomEntries()
            if (session.referenceVersion < STORAGE_VERSION) {
                dao.deleteBuiltinEntries()
                dao.deleteBuiltinCategories()
            }
            if (session.referenceVersion < STORAGE_VERSION || session.referenceLanguage != language) {
                dao.renameCustomCategories(Strings.get(R.string.ref_my_notes))
                dao.renameTldrCategories(Strings.get(R.string.ref_tldr_category))
            }
        }
        session.referenceVersion = STORAGE_VERSION
        session.referenceLanguage = language
    }

    private suspend fun moveOrphanedCustomEntries() {
        val orphaned = dao.getCustomEntries().filterNot { it.categoryId.startsWith(CUSTOM_PREFIX) }
        if (orphaned.isEmpty()) return
        val moved = orphaned.map { entry -> entry.copy(categoryId = ensureCustomCategory(entry.tech)) }
        dao.insertEntries(moved)
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
            return TldrResult.Failed(Strings.get(R.string.tldr_load_failed))
        } ?: return TldrResult.NotFound

        val parsed = TldrParser.parse(command, page.markdown)
        val languageNote = if (page.language == "ru" || !Strings.isRussian()) "" else Strings.get(R.string.tldr_english_note)

        database.withTransaction {
            ensureCategory(
                id = RefCategoryIds.TLDR_LINUX,
                tech = RefTech.LINUX,
                title = Strings.get(R.string.ref_tldr_category),
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
            title = Strings.get(R.string.ref_my_notes),
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
        const val STORAGE_VERSION = 2
        const val CUSTOM_PREFIX = "custom_"
        const val CUSTOM_CATEGORY_POSITION = 10_000
        const val TLDR_CATEGORY_POSITION = 10_001
    }
}

private fun String?.toRefSource(): RefSource = when (this) {
    RefEntryEntity.SOURCE_CUSTOM -> RefSource.CUSTOM
    RefEntryEntity.SOURCE_TLDR -> RefSource.TLDR
    else -> RefSource.BUILTIN
}
