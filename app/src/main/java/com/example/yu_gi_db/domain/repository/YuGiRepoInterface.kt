package com.example.yu_gi_db.domain.repository

import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard

interface YuGiRepoInterface {
    suspend fun getSmallPlayingCards(query: String): List<SmallPlayingCard>
    suspend fun getLargePlayingCards(id: String): List<LargePlayingCard>
}