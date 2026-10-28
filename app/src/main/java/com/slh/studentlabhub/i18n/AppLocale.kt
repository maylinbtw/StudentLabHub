package com.slh.studentlabhub.i18n

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import com.slh.studentlabhub.domain.model.AppLanguage
import java.util.Locale

object AppLocale {

    fun resolve(language: AppLanguage): Locale = when (language) {
        AppLanguage.RU -> Locale.forLanguageTag("ru")
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.BE -> Locale.forLanguageTag("be")
        AppLanguage.SYSTEM -> systemLocale()
    }

    // Встроенный контент (справочник, демо-данные) для белорусского берётся русский
    fun isRussian(language: AppLanguage): Boolean = resolve(language).language in setOf("ru", "be")

    fun isBelarusian(language: AppLanguage): Boolean = resolve(language).language == "be"

    fun wrap(base: Context, language: AppLanguage): Context {
        val locale = resolve(language)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }

    private fun systemLocale(): Locale {
        val locales = Resources.getSystem().configuration.locales
        return if (locales.isEmpty) Locale.getDefault() else locales[0]
    }
}
