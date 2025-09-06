package com.example.yu_gi_db.domain.repository

import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import kotlinx.coroutines.flow.Flow

interface YuGiRepoInterface {

    /**
     * Recupera le carte dall'API (se necessario) e le salva nel database locale.
     * Questa funzione gestirà la logica di popolamento/aggiornamento.
     */
    suspend fun fetchAndSaveAllCards() // INVARIATA

    /**
     * Recupera una lista di anteprime di carte (SmallPlayingCard) per il set di default (es. LOB)
     * dal database locale.
     * Restituisce un Flow per osservare i cambiamenti.
     */
    fun getDefaultSetSmallCardsStream(): Flow<List<SmallPlayingCard>> // RINOMINATA e MODIFICATA (rimosso query param)

    /**
     * Recupera i dettagli completi di una singola carta (LargePlayingCard)
     * dal database locale usando il suo ID.
     */
    suspend fun getLargeCardById(cardId: Int): LargePlayingCard? // INVARIATA

    /**
     * Esegue una ricerca flessibile nel database locale basata sui criteri forniti
     * e restituisce una lista di anteprime di carte (SmallPlayingCard).
     * Restituisce un Flow per osservare i cambiamenti.
     * @param criteria I criteri di ricerca (nome, tipo, attributi, ecc.).
     */
    fun searchSmallCards(criteria: AdvancedSearchCriteria): Flow<List<SmallPlayingCard>> // NUOVA FUNZIONE DI RICERCA FLESSIBILE

    // Le vecchie funzioni di ricerca specifiche come getCardsByName, getCardsByAttribute, etc., sono state rimosse.
}
