package com.example.yu_gi_db.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Definisce l'istanza di DataStore a livello di top-level usando il delegate
// Il nome "user_preferences" sarà il nome del file in cui verranno salvate le preferenze.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

object LanguagePreferencesKeys {
    val APP_LANGUAGE = stringPreferencesKey("app_language")
}

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Valori costanti per le preferenze di lingua
    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ITALIAN = "it"
        const val LANGUAGE_SYSTEM = "system" // Default
    }

    val languagePreference: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LanguagePreferencesKeys.APP_LANGUAGE] ?: LANGUAGE_SYSTEM
        }

    suspend fun updateLanguagePreference(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LanguagePreferencesKeys.APP_LANGUAGE] = language
        }
    }
}
