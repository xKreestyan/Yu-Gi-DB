package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardListViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "CardListViewModel"

    // State for initial data loading (all cards for the main list)
    private val _isLoadingInitialData = MutableStateFlow(false)
    val isLoadingInitialData: StateFlow<Boolean> = _isLoadingInitialData.asStateFlow()

    private val _initialDataError = MutableStateFlow<String?>(null)
    val initialDataError: StateFlow<String?> = _initialDataError.asStateFlow()

    // State for the main list of small cards
    private val _smallCards = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val smallCards: StateFlow<List<SmallPlayingCard>> = _smallCards.asStateFlow()

    // State for the selected large card details
    private val _selectedLargeCard = MutableStateFlow<LargePlayingCard?>(null)
    val selectedLargeCard: StateFlow<LargePlayingCard?> = _selectedLargeCard.asStateFlow()

    private val _isLoadingLargeCard = MutableStateFlow(false)
    val isLoadingLargeCard: StateFlow<Boolean> = _isLoadingLargeCard.asStateFlow()

    private val _largeCardError = MutableStateFlow<String?>(null)
    val largeCardError: StateFlow<String?> = _largeCardError.asStateFlow()

    // --- NEW StateFlows for Search Functionality ---
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LargePlayingCard>>(emptyList())
    val searchResults: StateFlow<List<LargePlayingCard>> = _searchResults.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    private var currentSearchJob: Job? = null

    init {
        Log.d(_tag, "ViewModel initialized")
        triggerInitialDataLoad() // Load all cards initially
        observeSmallCards()      // Observe the stream of small cards
    }

    private fun observeSmallCards() {
        viewModelScope.launch {
            yuGiRepo.getSmallCardsStream()
                .catch { e ->
                    Log.e(_tag, "Error observing small cards: ${e.message}", e)
                    // Optionally update a specific error state for the main list
                }
                .collect { cards ->
                    Log.d(_tag, "Observed ${cards.size} small cards from DB.")
                    _smallCards.value = cards
                }
        }
    }

    fun triggerInitialDataLoad() {
        _isLoadingInitialData.value = true
        _initialDataError.value = null
        Log.d(_tag, "Triggering initial data load...")
        viewModelScope.launch {
            try {
                yuGiRepo.fetchAndSaveAllCards()
                Log.d(_tag, "Initial data load successful.")
            } catch (e: Exception) {
                Log.e(_tag, "Error fetching initial data: ${e.message}", e)
                _initialDataError.value = e.message ?: "Unknown error during initial data load"
            } finally {
                _isLoadingInitialData.value = false
            }
        }
    }

    fun fetchLargeCardById(cardId: Int) {
        _isLoadingLargeCard.value = true
        _largeCardError.value = null
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

    fun selectCardFromResults(card: LargePlayingCard) {
        _selectedLargeCard.value = card
        _isLoadingLargeCard.value = false // No loading involved
        _largeCardError.value = null    // Clear previous error
        Log.d(_tag, "Card selected directly from results: ${card.name}")
    }

    fun clearSelectedLargeCard() {
        _selectedLargeCard.value = null
        _isLoadingLargeCard.value = false
        _largeCardError.value = null
        Log.d(_tag, "Selected large card cleared.")
    }

    // --- NEW Search Functions ---

    private fun <T> executeSearch(
        queryParameter: T,
        searchFlowProvider: (T) -> Flow<List<LargePlayingCard>>
    ) {
        currentSearchJob?.cancel() // Cancel any previous search
        currentSearchJob = viewModelScope.launch {
            searchFlowProvider(queryParameter)
                .onStart {
                    Log.d(_tag, "Search started for: $queryParameter")
                    _isSearching.value = true
                    _searchError.value = null // Clear previous search error on new search start
                    _searchResults.value = emptyList() // Clear previous results immediately
                }
                .catch { e ->
                    Log.e(_tag, "Error during search for '$queryParameter': ${e.message}", e)
                    _searchError.value = e.message ?: "Unknown search error"
                    _searchResults.value = emptyList() // Ensure results are empty on error
                    // _isSearching will be handled by onCompletion
                }
                .onCompletion {
                    Log.d(_tag, "Search completed for: $queryParameter. IsSearching set to false.")
                    _isSearching.value = false
                }
                .collect { results ->
                    Log.d(_tag, "Search for '$queryParameter' yielded ${results.size} results.")
                    _searchResults.value = results
                }
        }
    }

    fun searchCardsByName(nameQuery: String) {
        if (nameQuery.isBlank()) {
            Log.d(_tag, "Name query is blank, clearing search results.")
            clearSearchResults()
            return
        }
        executeSearch(nameQuery, yuGiRepo::getCardsByName)
    }

    fun searchCardsByType(type: String) {
        if (type.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(type, yuGiRepo::getCardsByType)
    }

    fun searchCardsByHumanReadableType(hrTypeQuery: String) {
        if (hrTypeQuery.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(hrTypeQuery, yuGiRepo::getCardsByHumanReadableType)
    }

    fun searchCardsByFrameType(frameType: String) {
        if (frameType.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(frameType, yuGiRepo::getCardsByFrameType)
    }

    fun searchCardsByDescription(descQuery: String) {
        if (descQuery.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(descQuery, yuGiRepo::getCardsByDescription)
    }

    fun searchCardsByRace(race: String) {
        if (race.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(race, yuGiRepo::getCardsByRace)
    }

    fun searchCardsByLevel(level: Int) {
        // Assuming level 0 is not a valid search, or handle as needed
        if (level < 0) { // Or some other validation for Int
            clearSearchResults(); return
        }
        executeSearch(level, yuGiRepo::getCardsByLevel)
    }

    fun searchCardsByAtk(atk: Int) {
        if (atk < 0) { // ATK can be 0, but negative is invalid
            clearSearchResults(); return
        }
        executeSearch(atk, yuGiRepo::getCardsByAtk)
    }

    fun searchCardsByDef(def: Int) {
         if (def < 0) { // DEF can be 0, but negative is invalid
            clearSearchResults(); return
        }
        executeSearch(def, yuGiRepo::getCardsByDef)
    }

    fun searchCardsByAttribute(attributeName: String) {
        if (attributeName.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(attributeName, yuGiRepo::getCardsByAttribute)
    }

    fun searchCardsByTypeLine(typeLineQuery: String) {
        if (typeLineQuery.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(typeLineQuery, yuGiRepo::getCardsByTypeLine)
    }

    fun searchCardsBySetName(setNameQuery: String) {
        if (setNameQuery.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(setNameQuery, yuGiRepo::getCardsBySetName) // Corrected here
    }

    fun searchCardsBySetRarity(rarityQuery: String) {
        if (rarityQuery.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(rarityQuery, yuGiRepo::getCardsBySetRarity)
    }

    fun searchCardsBySetCode(setCodeQuery: String) {
        if (setCodeQuery.isBlank()) {
            clearSearchResults(); return
        }
        executeSearch(setCodeQuery, yuGiRepo::getCardsBySetCode)
    }

    fun clearSearchResults() {
        currentSearchJob?.cancel()
        _searchResults.value = emptyList()
        _searchError.value = null
        _isSearching.value = false // Explicitly set for immediate UI feedback
        Log.d(_tag, "Search results cleared.")
    }

    override fun onCleared() {
        super.onCleared()
        currentSearchJob?.cancel() // Ensure job is cancelled when ViewModel is cleared
        Log.d(_tag, "ViewModel cleared, search job cancelled.")
    }
}
