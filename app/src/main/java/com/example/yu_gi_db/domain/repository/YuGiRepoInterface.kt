package com.example.yu_gi_db.domain.repository

import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import kotlinx.coroutines.flow.Flow


interface YuGiRepoInterface {

    /**
     * Recupera le carte dall'API (se necessario) e le salva nel database locale.
     * Questa funzione gestirà la logica di popolamento/aggiornamento.
     */
    suspend fun fetchAndSaveAllCards()

    /**
     * Recupera una lista di anteprime di carte (SmallPlayingCard) dal database locale.
     * Può includere una query per la ricerca.
     * Restituisce un Flow per osservare i cambiamenti.
     */
    fun getSmallCardsStream(query: String? = null): Flow<List<SmallPlayingCard>>

    /**
     * Recupera i dettagli completi di una singola carta (LargePlayingCard)
     * dal database locale usando il suo ID.
     */
    suspend fun getLargeCardById(cardId: Int): LargePlayingCard?

    // --- Funzioni di Ricerca Avanzata nel Database Locale ---

    fun getCardsByName(cardNameQuery: String): Flow<List<LargePlayingCard>>

    fun getCardsByAttribute(attributeName: String): Flow<List<LargePlayingCard>>

    fun getCardsByType(type: String): Flow<List<LargePlayingCard>>

    fun getCardsByHumanReadableType(hrTypeQuery: String): Flow<List<LargePlayingCard>>

    fun getCardsByFrameType(frameType: String): Flow<List<LargePlayingCard>>

    fun getCardsByDescription(descQuery: String): Flow<List<LargePlayingCard>>

    fun getCardsByRace(race: String): Flow<List<LargePlayingCard>>

    fun getCardsByLevel(level: Int): Flow<List<LargePlayingCard>>

    fun getCardsByAtk(atk: Int): Flow<List<LargePlayingCard>>

    fun getCardsByDef(def: Int): Flow<List<LargePlayingCard>>

    fun getCardsByTypeLine(typeLineQuery: String): Flow<List<LargePlayingCard>>

    fun getCardsBySetName(setNameQuery: String): Flow<List<LargePlayingCard>> // Rinominata da getCardsBySetNameQuery per coerenza

    fun getCardsBySetRarity(rarityQuery: String): Flow<List<LargePlayingCard>>

    fun getCardsBySetCode(setCodeQuery: String): Flow<List<LargePlayingCard>>

}
