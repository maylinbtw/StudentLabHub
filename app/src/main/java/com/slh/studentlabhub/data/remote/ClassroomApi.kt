package com.slh.studentlabhub.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.ZoneOffset
import java.time.ZonedDateTime
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

class UnauthorizedException : Exception("Access token is invalid")

class ClassroomApiException(
    val code: Int,
    message: String,
) : Exception(message)

data class RemoteCourse(
    val id: String,
    val name: String,
    val section: String?,
    val link: String?,
)

data class RemoteCourseWork(
    val id: String,
    val courseId: String,
    val title: String,
    val dueAt: Long?,
    val maxPoints: Double?,
    val link: String?,
)

data class RemoteSubmission(
    val courseWorkId: String,
    val state: String,
    val assignedGrade: Double?,
    val isLate: Boolean,
)

class ClassroomApi {

    suspend fun fetchUserEmail(accessToken: String): String {
        val json = get(USERINFO_URL, accessToken)
        val email = json.optStringOrNull("email")
        return email ?: throw ClassroomApiException(0, "Google did not return an email")
    }

    suspend fun fetchCourses(accessToken: String): List<RemoteCourse> =
        fetchPaged(
            baseUrl = "$BASE_URL/courses?studentId=me&courseStates=ACTIVE&pageSize=100",
            accessToken = accessToken,
            arrayKey = "courses",
        ) { item ->
            RemoteCourse(
                id = item.getString("id"),
                name = item.optStringOrNull("name") ?: Strings.get(R.string.untitled),
                section = item.optStringOrNull("section"),
                link = item.optStringOrNull("alternateLink"),
            )
        }

    suspend fun fetchTeacherNames(accessToken: String, courseId: String): List<String> =
        fetchPaged(
            baseUrl = "$BASE_URL/courses/$courseId/teachers?pageSize=30",
            accessToken = accessToken,
            arrayKey = "teachers",
        ) { item ->
            item.optJSONObject("profile")
                ?.optJSONObject("name")
                ?.optStringOrNull("fullName")
                .orEmpty()
        }.filter { it.isNotBlank() }

    suspend fun fetchCourseWork(accessToken: String, courseId: String): List<RemoteCourseWork> =
        fetchPaged(
            baseUrl = "$BASE_URL/courses/$courseId/courseWork?pageSize=100",
            accessToken = accessToken,
            arrayKey = "courseWork",
        ) { item ->
            RemoteCourseWork(
                id = item.getString("id"),
                courseId = courseId,
                title = item.optStringOrNull("title") ?: Strings.get(R.string.task_default_title),
                dueAt = parseDue(item),
                maxPoints = if (item.has("maxPoints")) item.optDouble("maxPoints") else null,
                link = item.optStringOrNull("alternateLink"),
            )
        }

    suspend fun fetchSubmissions(accessToken: String, courseId: String): List<RemoteSubmission> =
        fetchPaged(
            baseUrl = "$BASE_URL/courses/$courseId/courseWork/-/studentSubmissions?userId=me&pageSize=100",
            accessToken = accessToken,
            arrayKey = "studentSubmissions",
        ) { item ->
            RemoteSubmission(
                courseWorkId = item.getString("courseWorkId"),
                state = item.optString("state"),
                assignedGrade = if (item.has("assignedGrade")) item.optDouble("assignedGrade") else null,
                isLate = item.optBoolean("late", false),
            )
        }

    private suspend fun <T> fetchPaged(
        baseUrl: String,
        accessToken: String,
        arrayKey: String,
        mapper: (JSONObject) -> T,
    ): List<T> {
        val result = mutableListOf<T>()
        var pageToken: String? = null
        do {
            val url = if (pageToken == null) {
                baseUrl
            } else {
                "$baseUrl&pageToken=${URLEncoder.encode(pageToken, "UTF-8")}"
            }
            val json = get(url, accessToken)
            val array = json.optJSONArray(arrayKey)
            if (array != null) {
                for (index in 0 until array.length()) {
                    result += mapper(array.getJSONObject(index))
                }
            }
            pageToken = json.optStringOrNull("nextPageToken")
        } while (pageToken != null)
        return result
    }

    private suspend fun get(url: String, accessToken: String): JSONObject = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 20_000
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Accept", "application/json")

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

            when {
                code == 401 -> throw UnauthorizedException()
                code !in 200..299 -> throw ClassroomApiException(code, "Classroom API: HTTP $code")
                else -> JSONObject(body.ifBlank { "{}" })
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseDue(item: JSONObject): Long? {
        val date = item.optJSONObject("dueDate") ?: return null
        val time = item.optJSONObject("dueTime")
        val hours = if (time != null) time.optInt("hours", 0) else 23
        val minutes = if (time != null) time.optInt("minutes", 0) else 59
        return runCatching {
            ZonedDateTime.of(
                date.getInt("year"),
                date.getInt("month"),
                date.getInt("day"),
                hours,
                minutes,
                0,
                0,
                ZoneOffset.UTC,
            ).toInstant().toEpochMilli()
        }.getOrNull()
    }

    private companion object {
        const val BASE_URL = "https://classroom.googleapis.com/v1"
        const val USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo"
    }
}

private fun JSONObject.optStringOrNull(key: String): String? {
    if (!has(key) || isNull(key)) return null
    return optString(key).takeIf { it.isNotBlank() }
}