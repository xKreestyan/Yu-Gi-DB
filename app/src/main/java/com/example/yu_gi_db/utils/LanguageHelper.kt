package com.example.yu_gi_db.utils

import android.os.Build
import android.util.Log // AGGIUNTO
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.yu_gi_db.data.preferences.UserPreferencesRepository
import java.util.Locale

object LanguageHelper {

    private const val TAG = "LanguageHelper"

    fun applyAppLanguage(languageCode: String) {
        Log.d(TAG, "applyAppLanguage called with code: $languageCode") // Log
        val localeList: LocaleListCompat = when (languageCode) {
            UserPreferencesRepository.LANGUAGE_ENGLISH -> {
                LocaleListCompat.create(Locale.ENGLISH)
            }
            UserPreferencesRepository.LANGUAGE_ITALIAN -> {
                LocaleListCompat.create(Locale.ITALIAN)
            }
            UserPreferencesRepository.LANGUAGE_SYSTEM -> {
                LocaleListCompat.getEmptyLocaleList() // Usa il default di sistema
            }
            else -> { // Default di sicurezza al default di sistema
                Log.w(TAG, "Unknown language code '$languageCode', defaulting to system.")
                LocaleListCompat.getEmptyLocaleList()
            }
        }
        Log.d(TAG, "Setting application locales: ${localeList.toLanguageTags()}") // Log
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
