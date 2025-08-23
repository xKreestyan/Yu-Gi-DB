package com.example.yu_gi_db.viewmodels // MODIFICATO

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

    // Stato per la lista di carte piccole
    private val _smallCards = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val smallCards: StateFlow<List<SmallPlayingCard>> = _smallCards.asStateFlow()

    // Stato per indicare se il caricamento iniziale/popolamento del DB è in corso
    private val _isLoadingInitialData = MutableStateFlow(false)
    val isLoadingInitialData: StateFlow<Boolean> = _isLoadingInitialData.asStateFlow()

    // Stato per eventuali errori durante il fetch iniziale
    private val _initialDataError = MutableStateFlow<String?>(null)
    val initialDataError: StateFlow<String?> = _initialDataError.asStateFlow()

    // Flag in memoria per tracciare se il fetch iniziale è già stato tentato con successo in questa sessione
    private var initialDataFetchedSuccessfullyThisSession = false

    init {
        Log.d(_tag, "ViewModel initialized. Repo: $yuGiRepo")
        observeSmallCards() // Inizia ad osservare il flusso di carte dal repository

        // Controlla se i dati iniziali devono essere caricati.
        if (!initialDataFetchedSuccessfullyThisSession) {
            triggerInitialDataLoad()
        }
    }

    private fun observeSmallCards() {
        viewModelScope.launch {
            Log.d(_tag, "Inizio osservazione smallCards stream dal repository.")
            yuGiRepo.getSmallCardsStream(null) // Passa null o una query iniziale se necessario
                .catch { exception ->
                    Log.e(_tag, "Errore nell'osservare smallCards stream", exception)
                }
                .collect { cards ->
                    Log.d(_tag, "Ricevute ${cards.size} carte piccole. Aggiornamento UI.")
                    _smallCards.value = cards
                    if (_isLoadingInitialData.value && initialDataFetchedSuccessfullyThisSession) {
                        _isLoadingInitialData.value = false
                    }
                }
        }
    }
    fun triggerInitialDataLoad() {
        if (_isLoadingInitialData.value || initialDataFetchedSuccessfullyThisSession) {
            Log.d(_tag, "Caricamento iniziale già in corso o completato con successo in questa sessione. Skip.")
            return
        }

        viewModelScope.launch {
            _isLoadingInitialData.value = true
            _initialDataError.value = null
            Log.i(_tag, "Avvio fetchAndSaveAllCards per il popolamento iniziale del DB...")
            try {
                yuGiRepo.fetchAndSaveAllCards() // suspend function
                Log.i(_tag, "fetchAndSaveAllCards completato con successo.")
                initialDataFetchedSuccessfullyThisSession = true
            } catch (e: Exception) {
                Log.e(_tag, "Errore durante fetchAndSaveAllCards", e)
                _initialDataError.value = "Errore durante il caricamento dei dati: ${e.localizedMessage}"
                initialDataFetchedSuccessfullyThisSession = false
                _isLoadingInitialData.value = false
            }
        }
    }
}
