package com.slh.studentlabhub.data.backup

import androidx.room.withTransaction
import com.slh.studentlabhub.data.local.AppDatabase
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.RefCategoryEntity
import com.slh.studentlabhub.data.local.entity.RefEntryEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity
import com.slh.studentlabhub.domain.model.AppSettings
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.RefTech
import com.slh.studentlabhub.domain.model.WorkType
import com.slh.studentlabhub.domain.repository.ClassroomRepository
import com.slh.studentlabhub.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

data class BackupSummary(
    val subjects: Int,
    val works: Int,
    val articles: Int,
)

class BackupException(message: String) : Exception(message)

class BackupManager(
    private val database: AppDatabase,
    private val settingsRepository: SettingsRepository,
    private val classroomRepository: ClassroomRepository,
    private val archivedCourseIds: () -> Set<String>,
) {

    suspend fun export(output: OutputStream): BackupSummary = withContext(Dispatchers.IO) {
        val subjects = database.subjectDao().getAll()
        val labs = database.labDao().getAll()
        val categories = database.referenceDao().getCustomCategories()
        val entries = database.referenceDao().getCustomEntries()

        val root = JSONObject()
            .put("format", FORMAT)
            .put("version", VERSION)
            .put("createdAt", System.currentTimeMillis())
            .put("subjects", JSONArray(subjects.map { it.toJson() }))
            .put("labs", JSONArray(labs.map { it.toJson() }))
            .put("refCategories", JSONArray(categories.map { it.toJson() }))
            .put("refEntries", JSONArray(entries.map { it.toJson() }))
            .put("settings", settingsRepository.settings.value.toJson())
            .put("redoThreshold", classroomRepository.redoThreshold.value)
            .put("archivedCourses", JSONArray(archivedCourseIds().toList()))

        output.bufferedWriter(Charsets.UTF_8).use { it.write(root.toString(2)) }
        BackupSummary(subjects = subjects.size, works = labs.size, articles = entries.size)
    }

    suspend fun import(input: InputStream): BackupSummary = withContext(Dispatchers.IO) {
        val text = input.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = runCatching { JSONObject(text) }.getOrNull()
            ?: throw BackupException("Файл повреждён или это не резервная копия")
        if (root.optString("format") != FORMAT) {
            throw BackupException("Это не резервная копия StudentLabHub")
        }

        val subjects = root.optJSONArray("subjects").objects().map { it.toSubject() }
        val labs = root.optJSONArray("labs").objects().map { it.toLab() }
        val categories = root.optJSONArray("refCategories").objects().map { it.toCategory() }
        val entries = root.optJSONArray("refEntries").objects().map { it.toEntry() }

        database.withTransaction {
            database.subjectDao().deleteAll()
            if (subjects.isNotEmpty()) database.subjectDao().insertAll(subjects)
            if (labs.isNotEmpty()) database.labDao().insertAll(labs)
            if (categories.isNotEmpty()) database.referenceDao().insertCategories(categories)
            if (entries.isNotEmpty()) database.referenceDao().insertEntries(entries)
        }

        root.optJSONObject("settings")?.let { json ->
            settingsRepository.update { current -> json.toSettings(current) }
        }
        if (root.has("redoThreshold")) {
            classroomRepository.setRedoThreshold(root.optInt("redoThreshold", 1))
        }
        val archived = root.optJSONArray("archivedCourses").strings()
        classroomRepository.setCoursesArchived(archived, archived = true)

        BackupSummary(subjects = subjects.size, works = labs.size, articles = entries.size)
    }

    private companion object {
        const val FORMAT = "studentlabhub-backup"
        const val VERSION = 1
    }
}

private fun JSONArray?.objects(): List<JSONObject> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { optJSONObject(it) }
}

private fun JSONArray?.strings(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { optString(it).takeIf { value -> value.isNotEmpty() } }
}

private fun JSONObject.optStringOrNull(key: String): String? =
    if (has(key) && !isNull(key)) optString(key) else null

private fun JSONObject.optLongOrNull(key: String): Long? =
    if (has(key) && !isNull(key)) optLong(key) else null

private fun JSONObject.optIntOrNull(key: String): Int? =
    if (has(key) && !isNull(key)) optInt(key) else null

private inline fun <reified T : Enum<T>> JSONObject.optEnum(key: String, default: T): T {
    val name = optStringOrNull(key) ?: return default
    return enumValues<T>().firstOrNull { it.name == name } ?: default
}

private fun SubjectEntity.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("title", title)
    .put("semester", semester)
    .put("createdAt", createdAt)
    .put("classroomCourseId", classroomCourseId ?: JSONObject.NULL)
    .put("archived", archived)

private fun JSONObject.toSubject(): SubjectEntity = SubjectEntity(
    id = getLong("id"),
    title = optString("title", "Без названия"),
    semester = optInt("semester", 1),
    createdAt = optLong("createdAt", System.currentTimeMillis()),
    classroomCourseId = optStringOrNull("classroomCourseId"),
    archived = optBoolean("archived", false),
)

private fun LabEntity.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("subjectId", subjectId)
    .put("type", type.name)
    .put("number", number)
    .put("title", title)
    .put("status", status.name)
    .put("score", score ?: JSONObject.NULL)
    .put("notes", notes)
    .put("deadline", deadline ?: JSONObject.NULL)
    .put("classroomTaskId", classroomTaskId ?: JSONObject.NULL)
    .put("link", link ?: JSONObject.NULL)
    .put("needsRedo", needsRedo)

private fun JSONObject.toLab(): LabEntity = LabEntity(
    id = getLong("id"),
    subjectId = getLong("subjectId"),
    type = optEnum("type", WorkType.LAB),
    number = optInt("number", 1),
    title = optString("title", ""),
    status = optEnum("status", LabStatus.NOT_DONE),
    score = optIntOrNull("score"),
    notes = optString("notes", ""),
    deadline = optLongOrNull("deadline"),
    classroomTaskId = optStringOrNull("classroomTaskId"),
    link = optStringOrNull("link"),
    needsRedo = optBoolean("needsRedo", false),
)

private fun RefCategoryEntity.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("tech", tech.name)
    .put("title", title)
    .put("position", position)

private fun JSONObject.toCategory(): RefCategoryEntity = RefCategoryEntity(
    id = getString("id"),
    tech = optEnum("tech", RefTech.LINUX),
    title = optString("title", "Мои заметки"),
    position = optInt("position", 10_000),
)

private fun RefEntryEntity.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("categoryId", categoryId)
    .put("tech", tech.name)
    .put("title", title)
    .put("description", description)
    .put("example", example)
    .put("position", position)
    .put("source", source ?: JSONObject.NULL)

private fun JSONObject.toEntry(): RefEntryEntity = RefEntryEntity(
    id = getString("id"),
    categoryId = getString("categoryId"),
    tech = optEnum("tech", RefTech.LINUX),
    title = optString("title", ""),
    description = optString("description", ""),
    example = optString("example", ""),
    position = optInt("position", 0),
    source = optStringOrNull("source") ?: RefEntryEntity.SOURCE_CUSTOM,
)

private fun AppSettings.toJson(): JSONObject = JSONObject()
    .put("accent", accent.name)
    .put("themeMode", themeMode.name)
    .put("background", background.name)
    .put("dotShape", dotShape.name)
    .put("startScreen", startScreen.name)
    .put("remindersEnabled", remindersEnabled)
    .put("reminderTiming", reminderTiming.name)
    .put("redoAlerts", redoAlerts)
    .put("backgroundSync", backgroundSync)
    .put("largeText", largeText)

private fun JSONObject.toSettings(current: AppSettings): AppSettings = AppSettings(
    accent = optEnum("accent", current.accent),
    themeMode = optEnum("themeMode", current.themeMode),
    background = optEnum("background", current.background),
    dotShape = optEnum("dotShape", current.dotShape),
    startScreen = optEnum("startScreen", current.startScreen),
    remindersEnabled = optBoolean("remindersEnabled", current.remindersEnabled),
    reminderTiming = optEnum("reminderTiming", current.reminderTiming),
    redoAlerts = optBoolean("redoAlerts", current.redoAlerts),
    backgroundSync = optBoolean("backgroundSync", current.backgroundSync),
    largeText = optBoolean("largeText", current.largeText),
)

