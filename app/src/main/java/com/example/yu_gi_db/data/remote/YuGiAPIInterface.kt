package com.example.yu_gi_db.data.remote

import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard

interface YuGiAPIInterface {
    suspend fun getSmallPlayingCards(query: String): List<SmallPlayingCard>
    suspend fun getLargePlayingCards(id: String): List<LargePlayingCard>
}