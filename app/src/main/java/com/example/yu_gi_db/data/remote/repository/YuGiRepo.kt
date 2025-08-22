package com.example.yu_gi_db.data.remote.repository

import android.app.Application
import com.example.yu_gi_db.R
import com.example.yu_gi_db.data.local.db.dao.YuGiDAO // Import aggiunto
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import javax.inject.Inject

class YuGiRepo @Inject constructor(
    private val appContext: Application,
    private val yuGiDAO: YuGiDAO // Parametro YuGiDAO aggiunto
): YuGiRepoInterface {

    init {
        val appName = appContext.getString(R.string.app_name)
        println("Hello from the repository $appName. DAO instance: $yuGiDAO") // Aggiornato per mostrare il DAO
    }

    override suspend fun fetchAndSaveLargePlayingCards() {
        TODO("Not yet implemented")
    }

    override suspend fun getSmallPlayingCards(query: String): List<SmallPlayingCard> {
        TODO("Not yet implemented")
    }

    override suspend fun getLargePlayingCards(id: String): List<LargePlayingCard> {
        TODO("Not yet implemented")
    }
}
