package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.AdvancedSearchCriteria // Assicurati che l'import sia corretto
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    // --- Stati per la NUOVA Ricerca Avanzata ---
    private val _searchCriteria = MutableStateFlow(AdvancedSearchCriteria())
    val searchCriteria: StateFlow<AdvancedSearchCriteria> = _searchCriteria.asStateFlow()

    private val _isSearchingAdvanced = MutableStateFlow(false)
    val isSearchingAdvanced: StateFlow<Boolean> = _isSearchingAdvanced.asStateFlow()

    private val _advancedSearchResults = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val advancedSearchResults: StateFlow<List<SmallPlayingCard>> = _advancedSearchResults.asStateFlow()

    private val _advancedSearchError = MutableStateFlow<String?>(null)
    val advancedSearchError: StateFlow<String?> = _advancedSearchError.asStateFlow()

    private var advancedSearchJob: Job? = null

    // --- Stati per la Carta Selezionata (Dettagli) ---
    private val _selectedLargeCard = MutableStateFlow<LargePlayingCard?>(null)
    val selectedLargeCard: StateFlow<LargePlayingCard?> = _selectedLargeCard.asStateFlow()

    private val _isLoadingLargeCard = MutableStateFlow(false)
    val isLoadingLargeCard: StateFlow<Boolean> = _isLoadingLargeCard.asStateFlow()

    private val _largeCardError = MutableStateFlow<String?>(null)
    val largeCardError: StateFlow<String?> = _largeCardError.asStateFlow()

    // --- Stati per le Carte Preferite ---
    private val _favoriteCards = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val favoriteCards: StateFlow<List<SmallPlayingCard>> = _favoriteCards.asStateFlow()

    private val _favoritesError = MutableStateFlow<String?>(null)
    val favoritesError: StateFlow<String?> = _favoritesError.asStateFlow()

    init {
        Log.d(_tag, "ViewModel initialized")
        triggerInitialDataLoad() // Carica i dati dall'API al DB se necessario
        observeSmallCards()      // Osserva le carte del set LOB (_smallCards) dal DB
        observeAndPerformAdvancedSearch() // Inizia ad ascoltare i cambiamenti dei criteri per la ricerca avanzata
        observeFavoriteCards()      // Osserva le carte preferite
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

    // --- Logica per la Ricerca Avanzata ---

    fun updateAdvancedSearchCriteria(newCriteria: AdvancedSearchCriteria) {
        _searchCriteria.value = newCriteria
    }

    private fun AdvancedSearchCriteria.isEffectivelyEmpty(): Boolean {
        return name.isNullOrBlank() &&
               type.isNullOrBlank() &&
               attribute.isNullOrBlank() &&
               level == null &&
               atkMin == null && atkMax == null &&
               defMin == null && defMax == null &&
               idQuery.isNullOrBlank() // AGGIUNTO CONTROLLO PER idQuery
    }

    private fun observeAndPerformAdvancedSearch() {
        advancedSearchJob?.cancel()
        advancedSearchJob = _searchCriteria
            .debounce(350L) 
            .distinctUntilChanged()
            .onEach { criteria ->
                if (criteria.isEffectivelyEmpty()) {
                    Log.d(_tag, "Advanced search criteria are empty. Clearing advanced search results.")
                    _isSearchingAdvanced.value = false
                    _advancedSearchError.value = null
                    _advancedSearchResults.value = emptyList()
                } else {
                    Log.d(_tag, "Advanced search criteria updated: $criteria. Setting isSearchingAdvanced=true.")
                    _isSearchingAdvanced.value = true
                    _advancedSearchError.value = null 
                }
            }
            .flatMapLatest { criteria ->
                if (criteria.isEffectivelyEmpty()) {
                    kotlinx.coroutines.flow.flowOf(emptyList<SmallPlayingCard>())
                } else {
                    Log.d(_tag, "flatMapLatest: Executing ADVANCED search for criteria: $criteria")
                    yuGiRepo.searchSmallCards(criteria)
                        .catch { e ->
                            Log.e(_tag, "Error from ADVANCED searchSmallCards for '$criteria': ${e.message}", e)
                            _advancedSearchError.value = e.message ?: "Unknown advanced search error"
                            emit(emptyList<SmallPlayingCard>())
                        }
                }
            }
            .onEach { results ->
                Log.d(_tag, "Advanced search for '${_searchCriteria.value}' collected ${results.size} results.")
                _advancedSearchResults.value = results
                if (!_searchCriteria.value.isEffectivelyEmpty()) { 
                    _isSearchingAdvanced.value = false
                    Log.d(_tag, "Advanced search completed. isSearchingAdvanced set to false.")
                }
            }
            .launchIn(viewModelScope)
    }


    // --- Logica per la Selezione della Carta (Dettagli) ---
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

    // --- Logica per le Carte Preferite ---
    private fun observeFavoriteCards() {
        viewModelScope.launch {
            yuGiRepo.getFavoriteSmallCardsStream()
                .catch { e ->
                    Log.e(_tag, "Error observing favorite cards: ${e.message}", e)
                    _favoritesError.value = "Error loading favorite cards: ${e.message}"
                }
                .collect { cards ->
                    Log.d(_tag, "Observed ${cards.size} favorite cards.")
                    _favoriteCards.value = cards
                    _favoritesError.value = null // Pulisci errore precedente se i dati arrivano
                }
        }
    }

    fun toggleFavoriteStatus(cardId: Int) {
        viewModelScope.launch {
            try {
                Log.d(_tag, "Toggling favorite status for card ID: $cardId")
                yuGiRepo.toggleFavoriteStatus(cardId)
            } catch (e: Exception) {
                Log.e(_tag, "Error toggling favorite status for card ID $cardId: ${e.message}", e)
                // Considera di esporre questo errore se necessario
                _favoritesError.value = "Error updating favorite status: ${e.message}" // Ad esempio
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        advancedSearchJob?.cancel()
        Log.d(_tag, "ViewModel cleared, advancedSearchJob cancelled.")
    }
}
