package com.slh.studentlabhub.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class TldrPage(
    val command: String,
    val language: String,
    val markdown: String,
)

class TldrApi {

    suspend fun fetch(command: String): TldrPage? = withContext(Dispatchers.IO) {
        for ((language, folder) in CANDIDATES) {
            val markdown = download("$BASE_URL/$folder/$command.md")
            if (markdown != null) {
                return@withContext TldrPage(
                    command = command,
                    language = language,
                    markdown = markdown,
                )
            }
        }
        null
    }

    private fun download(url: String): String? {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            return when (val code = connection.responseCode) {
                200 -> connection.inputStream.bufferedReader().use { it.readText() }
                404 -> null
                else -> throw IOException("tldr-pages: HTTP $code")
            }
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/tldr-pages/tldr/main"

        val CANDIDATES = listOf(
            "ru" to "pages.ru/linux",
            "ru" to "pages.ru/common",
            "en" to "pages/linux",
            "en" to "pages/common",
        )
    }
}
