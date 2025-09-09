package com.example.yu_gi_db

import android.os.Bundle
import android.util.Log // Import per Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope // Import per lifecycleScope
import com.example.yu_gi_db.data.preferences.UserPreferencesRepository // Import per Repository
import com.example.yu_gi_db.utils.LanguageHelper // Import per LanguageHelper
import com.example.yu_gi_db.views.InitMainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first // Import per .first()
import kotlinx.coroutines.launch // Import per launch
import javax.inject.Inject

private const val TAG_MAIN_ACTIVITY = "MainActivity" // TAG per i log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject // Iniezione del repository
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lancia una coroutine per gestire la logica asincrona della lingua
        lifecycleScope.launch {
            // Leggi la preferenza della lingua (impostane una di default nel repo se non esiste)
            val currentLanguage = userPreferencesRepository.languagePreference.first()
            Log.d(TAG_MAIN_ACTIVITY, "onCreate - Applying language from DataStore: $currentLanguage")
            LanguageHelper.applyAppLanguage(currentLanguage) // Applica la lingua

            // Queste operazioni UI vengono eseguite dopo che la lingua è stata impostata
            enableEdgeToEdge()
            setContent {
                InitMainScreen()
            }
        }
    }
}
