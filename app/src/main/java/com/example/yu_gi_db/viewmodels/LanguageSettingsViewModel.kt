package com.example.yu_gi_db.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Espone la preferenza della lingua corrente come StateFlow
    val currentLanguage: StateFlow<String> = userPreferencesRepository.languagePreference
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Rende il Flow "caldo" mentre ci sono iscritti
            initialValue = UserPreferencesRepository.LANGUAGE_SYSTEM // Valore iniziale prima che il DataStore emetta
        )

    fun updateLanguage(newLanguageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguagePreference(newLanguageCode)
            // L'applicazione della lingua e la ricreazione dell'activity
            // verranno gestite nel Composable che osserva questo cambiamento,
            // o subito dopo questa chiamata nel Composable.
        }
    }
}
