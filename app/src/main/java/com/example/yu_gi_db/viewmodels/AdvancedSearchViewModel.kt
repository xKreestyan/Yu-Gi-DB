package com.example.yu_gi_db.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.AdvancedSearchCriteria
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
import javax.inject.Inject

@HiltViewModel
class AdvancedSearchViewModel @Inject constructor(
    private val yuGiRepo: YuGiRepoInterface
) : ViewModel() {

    private val _tag = "AdvancedSearchViewModel"

    private val _searchCriteria = MutableStateFlow(AdvancedSearchCriteria())
    val searchCriteria: StateFlow<AdvancedSearchCriteria> = _searchCriteria.asStateFlow()

    private val _isSearchingAdvanced = MutableStateFlow(false)
    val isSearchingAdvanced: StateFlow<Boolean> = _isSearchingAdvanced.asStateFlow()

    private val _advancedSearchResults = MutableStateFlow<List<SmallPlayingCard>>(emptyList())
    val advancedSearchResults: StateFlow<List<SmallPlayingCard>> = _advancedSearchResults.asStateFlow()

    private val _advancedSearchError = MutableStateFlow<String?>(null)
    val advancedSearchError: StateFlow<String?> = _advancedSearchError.asStateFlow()

    private var advancedSearchJob: Job? = null

    init {
        Log.d(_tag, "AdvancedSearchViewModel initialized")
        observeAndPerformAdvancedSearch() // Inizia ad ascoltare i cambiamenti dei criteri per la ricerca avanzata
    }

    fun updateAdvancedSearchCriteria(newCriteria: AdvancedSearchCriteria) {
        _searchCriteria.value = newCriteria
    }

    private fun AdvancedSearchCriteria.isEffectivelyEmpty(): Boolean {
        return name.isNullOrBlank() &&
                type.isNullOrBlank() &&
                raceQuery.isNullOrBlank() &&
                attribute.isNullOrBlank() &&
                level == null &&
                atkMin == null && atkMax == null &&
                defMin == null && defMax == null &&
                idQuery.isNullOrBlank() &&
                isFavorite == null &&
                setNameQuery.isNullOrBlank() &&
                setCodeQuery.isNullOrBlank()
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
                    kotlinx.coroutines.flow.flowOf(emptyList())
                } else {
                    Log.d(_tag, "flatMapLatest: Executing ADVANCED search for criteria: $criteria")
                    yuGiRepo.searchSmallCards(criteria)
                        .catch { e ->
                            Log.e(_tag, "Error from ADVANCED searchSmallCards for '$criteria': ${e.message}", e)
                            _advancedSearchError.value = e.message ?: "Unknown advanced search error"
                            emit(emptyList())
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

    override fun onCleared() {
        super.onCleared()
        advancedSearchJob?.cancel()
        Log.d(_tag, "AdvancedSearchViewModel cleared.")
    }
}
    