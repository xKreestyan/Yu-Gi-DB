package com.example.yu_gi_db.viewmodels // MODIFICATO

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface // Cambiato da .data.remote.repository
import com.example.yu_gi_db.model.SmallPlayingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch // Aggiunto se mancava per .catch
import kotlinx.coroutines.flow.distinctUntilChanged // Aggiunto per coerenza con discussione precedente
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardListViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "CardListViewModel"

    private val _smallCards = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val smallCards: StateFlow<List<SmallPlayingCard>> = _smallCards.asStateFlow()

    private val _isLoadingInitialData = MutableStateFlow(false)
    val isLoadingInitialData: StateFlow<Boolean> = _isLoadingInitialData.asStateFlow()

    private val _initialDataError = MutableStateFlow<String?>(null)
    val initialDataError: StateFlow<String?> = _initialDataError.asStateFlow()

    private var initialDataFetchAttemptedThisSession = false // Rinominiamo per chiarezza

    init {
        Log.d(_tag, "ViewModel initialized. Repo: $yuGiRepo")
        observeSmallCards()
        // Non chiamare triggerInitialDataLoad qui se deve essere chiamato esplicitamente dalla UI
        // o basato su una logica più complessa. Per ora, lo lascio se l'intenzione è caricarlo all'avvio.
        // Se `initialDataFetchAttemptedThisSession` deve prevenire fetch multipli *automatici*,
        // allora la logica in triggerInitialDataLoad è più appropriata.
        triggerInitialDataLoad() // Se vuoi che si carichi all'avvio del ViewModel
    }

    private fun observeSmallCards() {
        viewModelScope.launch {
            Log.d(_tag, "Inizio osservazione smallCards stream dal repository.")
            yuGiRepo.getSmallCardsStream(null)
                .distinctUntilChanged() // Evita aggiornamenti inutili se la lista non cambia
                .catch { exception ->
                    Log.e(_tag, "Errore nell'osservare smallCards stream", exception)
                    // Potresti voler impostare _initialDataError qui se l'errore è critico per la visualizzazione
                    // e _isLoadingInitialData a false se questo errore interrompe un caricamento.
                }
                .collect { cards ->
                    Log.d(_tag, "Ricevute ${cards.size} carte piccole. Aggiornamento UI.")
                    _smallCards.value = cards
                    // Se il caricamento iniziale è terminato (con successo o fallimento),
                    // e abbiamo ricevuto carte, l'indicatore di caricamento principale
                    // dovrebbe già essere stato gestito da triggerInitialDataLoad.
                    // Questa collect aggiorna semplicemente la lista.
                    // Se _isLoadingInitialData è ancora true qui E initialDataFetchAttemptedThisSession è true,
                    // significa che triggerInitialDataLoad non ha completato il suo ciclo per qualche motivo
                    // o c'è una race condition.
                    if (_isLoadingInitialData.value && initialDataFetchAttemptedThisSession) {
                        Log.w(_tag, "Collect sta vedendo isLoadingInitialData ancora a true dopo che il fetch è stato tentato.")
                        // _isLoadingInitialData.value = false; // Forse una misura di sicurezza, ma dovrebbe essere gestito da trigger.
                    }
                }
        }
    }

    fun triggerInitialDataLoad() {
        // Previene fetch multipli se uno è già stato tentato in questa sessione
        // o se uno è attivamente in corso.
        if (initialDataFetchAttemptedThisSession || _isLoadingInitialData.value) {
            val reason = if (_isLoadingInitialData.value) "già in corso" else "già tentato in questa sessione"
            Log.d(_tag, "Caricamento iniziale skip: $reason.")
            return
        }

        viewModelScope.launch {
            _isLoadingInitialData.value = true
            _initialDataError.value = null
            initialDataFetchAttemptedThisSession = true // Marchia che un tentativo è iniziato
            var fetchSuccess = false // Traccia il successo del fetch

            Log.i(_tag, "Avvio fetchAndSaveAllCards per il popolamento iniziale del DB...")
            try {
                yuGiRepo.fetchAndSaveAllCards() // suspend function
                Log.i(_tag, "fetchAndSaveAllCards completato con successo.")
                fetchSuccess = true
            } catch (e: Exception) {
                Log.e(_tag, "Errore durante fetchAndSaveAllCards", e)
                _initialDataError.value = "Errore durante il caricamento dei dati: ${e.localizedMessage}"
                // fetchSuccess rimane false
            } finally {
                // Questo blocco finally assicura che isLoadingInitialData venga impostato a false
                // indipendentemente dal successo o fallimento del try-catch.
                _isLoadingInitialData.value = false
                Log.d(_tag, "Fine triggerInitialDataLoad. isLoadingInitialData settato a false. Successo Fetch: $fetchSuccess")
            }
        }
    }
}
