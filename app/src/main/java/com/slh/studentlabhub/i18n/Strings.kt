package com.slh.studentlabhub.i18n

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.slh.studentlabhub.domain.model.AppLanguage

object Strings {

    private var appContext: Context? = null
    private var languageProvider: () -> AppLanguage = { AppLanguage.SYSTEM }
    private var cached: Pair<AppLanguage, Context>? = null

    fun init(context: Context, language: () -> AppLanguage) {
        appContext = context.applicationContext
        languageProvider = language
        cached = null
    }

    fun ensureInitialized(context: Context) {
        if (appContext == null) appContext = context.applicationContext
    }

    fun localizedContext(): Context {
        val base = checkNotNull(appContext) { "Strings is not initialized" }
        val language = languageProvider()
        cached?.let { (cachedLanguage, context) ->
            if (cachedLanguage == language) return context
        }
        val context = AppLocale.wrap(base, language)
        cached = language to context
        return context
    }

    fun isRussian(): Boolean = AppLocale.isRussian(languageProvider())

    fun pick(ru: String, en: String): String = if (isRussian()) ru else en

    fun get(@StringRes id: Int, vararg args: Any): String =
        localizedContext().getString(id, *args)

    fun plural(@PluralsRes id: Int, count: Int, vararg args: Any): String =
        localizedContext().resources.getQuantityString(id, count, *args)
}

@Composable
fun pluralResource(@PluralsRes id: Int, count: Int, vararg args: Any): String =
    LocalContext.current.resources.getQuantityString(id, count, *args)
