package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.LargePlayingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "CardDetailViewModel"
    private val _selectedLargeCard = MutableStateFlow<LargePlayingCard?>(null)
    val selectedLargeCard: StateFlow<LargePlayingCard?> = _selectedLargeCard.asStateFlow()

    private val _isLoadingLargeCard = MutableStateFlow(false)
    val isLoadingLargeCard: StateFlow<Boolean> = _isLoadingLargeCard.asStateFlow()

    private val _largeCardError = MutableStateFlow<String?>(null)
    val largeCardError: StateFlow<String?> = _largeCardError.asStateFlow()

    // --- Stati per la Carta Selezionata (Dettagli) ---
    // Qui verranno spostati _selectedLargeCard, _isLoadingLargeCard, _largeCardError

    init {
        Log.d(_tag, "CardDetailViewModel initialized")
    }

    fun fetchLargeCardById(cardId: Int) {
        _isLoadingLargeCard.value = true
        _largeCardError.value = null
        _selectedLargeCard.value = null
        Log.d(_tag, "Fetching large card with ID: $cardId")
        viewModelScope.launch {
            try {
                val card = yuGiRepo.getLargeCardById(cardId)
                _selectedLargeCard.value = card
                if (card == null) {
                    Log.w(_tag, "No large card found with ID: $cardId")
                    _largeCardError.value = "Card not found"
                } else {
                    Log.d(_tag, "Successfully fetched large card: ${card.name}")
                }
            } catch (e: Exception) {
                Log.e(_tag, "Error fetching large card by ID $cardId: ${e.message}", e)
                _largeCardError.value = e.message ?: "Error fetching card details"
            } finally {
                _isLoadingLargeCard.value = false
            }
        }
    }

    fun clearSelectedLargeCard() {
        _selectedLargeCard.value = null
        Log.d(_tag, "Selected large card cleared.")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(_tag, "CardDetailViewModel cleared.")
    }
}
    