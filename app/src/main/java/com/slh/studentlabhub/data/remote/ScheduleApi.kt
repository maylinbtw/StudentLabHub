package com.slh.studentlabhub.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class ScheduleApi {

    /** Скачивает страницу расписания. Сайт отдаёт windows-1251, иногда только по http. */
    suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
        val candidates = buildList {
            add(url)
            if (url.startsWith("https://")) add("http://" + url.removePrefix("https://"))
            if (url.startsWith("http://")) add("https://" + url.removePrefix("http://"))
        }
        var lastError: IOException? = null
        for (candidate in candidates) {
            try {
                return@withContext download(candidate)
            } catch (error: IOException) {
                lastError = error
            }
        }
        throw lastError ?: IOException("Schedule is unavailable")
    }

    private fun download(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "StudentLabHub")
            setRequestProperty("Cache-Control", "no-cache")
        }
        try {
            val code = connection.responseCode
            if (code !in 200..299) throw IOException("HTTP $code")
            val bytes = connection.inputStream.use { it.readBytes() }
            return String(bytes, detectCharset(connection.contentType, bytes))
        } finally {
            connection.disconnect()
        }
    }

    private fun detectCharset(contentType: String?, bytes: ByteArray): Charset {
        val fromHeader = contentType?.let { Regex("charset=([\\w-]+)", RegexOption.IGNORE_CASE).find(it) }
            ?.groupValues?.get(1)
        val head = String(bytes, 0, minOf(bytes.size, 2048), Charsets.ISO_8859_1)
        val fromMeta = Regex("charset=[\"']?([\\w-]+)", RegexOption.IGNORE_CASE).find(head)?.groupValues?.get(1)
        val name = fromMeta ?: fromHeader ?: "windows-1251"
        return runCatching { Charset.forName(name) }.getOrDefault(Charset.forName("windows-1251"))
    }

    private companion object {
        const val TIMEOUT_MS = 15_000
    }
}
