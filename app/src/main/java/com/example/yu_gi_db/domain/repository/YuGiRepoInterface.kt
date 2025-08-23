package com.example.yu_gi_db.domain.repository

import com.example.yu_gi_db.model.LargePlayingCard // Probabilmente servirà
import com.example.yu_gi_db.model.SmallPlayingCard // Probabilmente servirà
// Importa Flow se usi getAllCards come Flow dal DAO
import kotlinx.coroutines.flow.Flow


interface YuGiRepoInterface {

    /**
     * Recupera le carte dall'API (se necessario) e le salva nel database locale.
     * Questa funzione gestirà la logica di popolamento/aggiornamento.
     */
    suspend fun fetchAndSaveAllCards() // Nome modificato per chiarezza


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


    // TODO: Aggiungere altre funzioni necessarie, come:
    // suspend fun getCardsBySet(setName: String): List<SmallPlayingCard>
    // fun getAllSetsStream(): Flow<List<SetEntity>> // SetEntity andrebbe definita se si vuole esporre
    // fun getAllTypeLinesStream(): Flow<List<TypeLineEntity>> // TypeLineEntity andrebbe definita se si vuole esporre
}
