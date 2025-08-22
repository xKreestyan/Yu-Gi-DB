package com.example.yu_gi_db.domain.repository

import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard

interface YuGiRepoInterface {

    suspend fun fetchAndSaveLargePlayingCards() //popolazione db
    suspend fun getSmallPlayingCards(query: String): List<SmallPlayingCard> //anteprima carte
    suspend fun getLargePlayingCards(id: String): List<LargePlayingCard> //vista completa della carta
}