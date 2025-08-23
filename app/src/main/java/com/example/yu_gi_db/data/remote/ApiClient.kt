package com.example.yu_gi_db.data.remote

import com.example.yu_gi_db.model.LargePlayingCardResponse

/**
 * Interfaccia per il client API.
 * Definisce il contratto per recuperare i dati dal server remoto.
 */
interface ApiClient {

    /**
     * Recupera le carte dall'API in base ai parametri forniti.
     * Questa funzione dovrebbe gestire la chiamata di rete e la deserializzazione della risposta.
     *
     * @param params Mappa di parametri di query da aggiungere all'URL dell'API.
     *               Esempio: mapOf("staple" to "yes") o mapOf("cardset" to "Legend of Blue Eyes White Dragon")
     * @return LargePlayingCardResponse che contiene la lista delle carte o null in caso di errore.
     */
    suspend fun fetchCards(params: Map<String, String>): LargePlayingCardResponse?
}
