package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.LargePlayingCard // Importa LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardListViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "CardListViewModel"

    // StateFlow per la lista di SmallPlayingCard
    private val _smallCards = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val smallCards: StateFlow<List<SmallPlayingCard>> = _smallCards.asStateFlow()

    // StateFlow per il caricamento iniziale dei dati
    private val _isLoadingInitialData = MutableStateFlow(false)
    val isLoadingInitialData: StateFlow<Boolean> = _isLoadingInitialData.asStateFlow()

    private val _initialDataError = MutableStateFlow<String?>(null)
    val initialDataError: StateFlow<String?> = _initialDataError.asStateFlow()

    private var initialDataFetchAttemptedThisSession = false

    // StateFlow per la LargePlayingCard selezionata
    private val _selectedLargeCard = MutableStateFlow<LargePlayingCard?>(null)
    val selectedLargeCard: StateFlow<LargePlayingCard?> = _selectedLargeCard.asStateFlow()

    // StateFlow per lo stato di caricamento della LargePlayingCard
    private val _isLoadingLargeCard = MutableStateFlow(false)
    val isLoadingLargeCard: StateFlow<Boolean> = _isLoadingLargeCard.asStateFlow()

    // StateFlow per errori durante il caricamento della LargePlayingCard
    private val _largeCardError = MutableStateFlow<String?>(null)
    val largeCardError: StateFlow<String?> = _largeCardError.asStateFlow()

    init {
        Log.d(_tag, "ViewModel initialized. Repo: $yuGiRepo")
        observeSmallCards()
        triggerInitialDataLoad()
    }

    private fun observeSmallCards() {
        viewModelScope.launch {
            Log.d(_tag, "Inizio osservazione smallCards stream dal repository.")
            yuGiRepo.getSmallCardsStream(null)
                .distinctUntilChanged()
                .catch { exception ->
                    Log.e(_tag, "Errore nell'osservare smallCards stream", exception)
                }
                .collect { cards ->
                    Log.d(_tag, "Ricevute ${cards.size} carte piccole. Aggiornamento UI.")
                    _smallCards.value = cards
                    if (_isLoadingInitialData.value && initialDataFetchAttemptedThisSession) {
                        Log.w(_tag, "Collect sta vedendo isLoadingInitialData ancora a true dopo che il fetch è stato tentato.")
                    }
                }
        }
    }

    fun triggerInitialDataLoad() {
        if (initialDataFetchAttemptedThisSession || _isLoadingInitialData.value) {
            val reason = if (_isLoadingInitialData.value) "già in corso" else "già tentato in questa sessione"
            Log.d(_tag, "Caricamento iniziale skip: $reason.")
            return
        }

        viewModelScope.launch {
            _isLoadingInitialData.value = true
            _initialDataError.value = null
            initialDataFetchAttemptedThisSession = true
            var fetchSuccess = false

            Log.i(_tag, "Avvio fetchAndSaveAllCards per il popolamento iniziale del DB...")
            try {
                yuGiRepo.fetchAndSaveAllCards()
                Log.i(_tag, "fetchAndSaveAllCards completato con successo.")
                fetchSuccess = true
            } catch (e: Exception) {
                Log.e(_tag, "Errore durante fetchAndSaveAllCards", e)
                _initialDataError.value = "Errore durante il caricamento dei dati: ${e.localizedMessage}"
            } finally {
                _isLoadingInitialData.value = false
                Log.d(_tag, "Fine triggerInitialDataLoad. isLoadingInitialData settato a false. Successo Fetch: $fetchSuccess")
            }
        }
    }

    fun fetchLargeCardById(cardId: Int) {
        viewModelScope.launch {
            _isLoadingLargeCard.value = true
            _largeCardError.value = null
            // _selectedLargeCard.value = null // Opzionale: pulire la carta precedente all'inizio del fetch
                                            // Dipende se vuoi che la vecchia carta scompaia subito
                                            // o solo quando la nuova è pronta (o c'è un errore).
                                            // Per ora lo commento, implica che la vecchia carta resta visibile durante il caricamento della nuova.
                                            // Se la nuova non viene trovata, _selectedLargeCard non verrà aggiornata.

            Log.d(_tag, "Avvio fetchLargeCardById per ID: $cardId")
            try {
                val card = yuGiRepo.getLargeCardById(cardId)
                if (card != null) {
                    Log.i(_tag, "LargePlayingCard con ID: $cardId trovata: ${card.name}")
                    _selectedLargeCard.value = card // Aggiorna solo se la carta è trovata
                } else {
                    Log.w(_tag, "Nessuna LargePlayingCard trovata per ID: $cardId")
                    _selectedLargeCard.value = null // Assicura che se la carta non viene trovata, lo stato sia null
                    _largeCardError.value = "Carta non trovata con ID: $cardId."
                }
            } catch (e: Exception) {
                Log.e(_tag, "Errore durante fetchLargeCardById per ID: $cardId", e)
                 _selectedLargeCard.value = null // Assicura che in caso di errore non ci sia una carta selezionata
                _largeCardError.value = "Errore durante il caricamento della carta ID $cardId: ${e.localizedMessage}"
            } finally {
                _isLoadingLargeCard.value = false
                Log.d(_tag, "Fine fetchLargeCardById per ID: $cardId. isLoadingLargeCard settato a false.")
            }
        }
    }

    fun clearSelectedLargeCard() {
        _selectedLargeCard.value = null
        _largeCardError.value = null // Pulisce anche eventuali errori relativi
        _isLoadingLargeCard.value = false // Assicura che anche lo stato di caricamento sia resettato
        Log.d(_tag, "selectedLargeCard e i relativi stati sono stati resettati.")
    }
}
