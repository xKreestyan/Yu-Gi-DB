package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "FavoritesViewModel"

    init {
        Log.d(_tag, "FavoritesViewModel initialized") }

    fun toggleFavoriteStatus(cardId: Int) {
        viewModelScope.launch {
            try {
                Log.d(_tag, "Toggling favorite status for card ID: $cardId")
                yuGiRepo.toggleFavoriteStatus(cardId)
                // L'aggiornamento della UI dei preferiti dipenderà da come le altre liste (default o ricerca)
                // vengono ricaricate o come lo stato isFavorite viene propagato.
            } catch (e: Exception) {
                Log.e(_tag, "Error toggling favorite status for card ID $cardId: ${e.message}", e)
                // Gestisci l'errore, magari con un evento verso la UI se necessario
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(_tag, "FavoritesViewModel cleared.")
    }
}
    