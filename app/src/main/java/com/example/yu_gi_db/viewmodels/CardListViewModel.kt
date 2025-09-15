package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.SmallPlayingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardListViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "CardListViewModel"

    // --- Stati per il Caricamento API Iniziale e Lista di Default (LOB) ---
    private val _isLoadingInitialData = MutableStateFlow(false)
    val isLoadingInitialData: StateFlow<Boolean> = _isLoadingInitialData.asStateFlow()

    private val _initialDataError = MutableStateFlow<String?>(null)
    val initialDataError: StateFlow<String?> = _initialDataError.asStateFlow()

    private val _smallCards = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val smallCards: StateFlow<List<SmallPlayingCard>> = _smallCards.asStateFlow()

    init {
        Log.d(_tag, "ViewModel initialized")
        triggerInitialDataLoad() // Carica i dati dall'API al DB se necessario
        observeSmallCards()      // Osserva le carte del set LOB (_smallCards) dal DB
    }

    fun triggerInitialDataLoad() {
        _isLoadingInitialData.value = true
        _initialDataError.value = null
        Log.d(_tag, "Triggering initial data load (API fetch)...")
        viewModelScope.launch {
            try {
                yuGiRepo.fetchAndSaveAllCards()
                Log.d(_tag, "Initial data load (API fetch) successful.")
            } catch (e: Exception) {
                Log.e(_tag, "Error fetching initial API data: ${e.message}", e)
                _initialDataError.value = e.message ?: "Unknown error during initial API data fetch"
            } finally {
                _isLoadingInitialData.value = false
            }
        }
    }

    private fun observeSmallCards() { // Osserva il set di default LOB
        viewModelScope.launch {
            yuGiRepo.getDefaultSetSmallCardsStream()
                .catch { e ->
                    Log.e(_tag, "Error observing default set (_smallCards): ${e.message}", e)
                    _initialDataError.value = "Error loading default cards: ${e.message}" // Può sovrascrivere errore API fetch
                }
                .collect { cards ->
                    Log.d(_tag, "Observed ${cards.size} default set cards for _smallCards.")
                    _smallCards.value = cards
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(_tag, "ViewModel cleared.")
    }
}
