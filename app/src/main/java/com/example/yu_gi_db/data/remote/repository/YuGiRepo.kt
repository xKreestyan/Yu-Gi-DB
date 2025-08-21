package com.example.yu_gi_db.data.remote.repository

import android.app.Application
import com.example.yu_gi_db.R
import com.example.yu_gi_db.data.remote.YuGiAPIInterface
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard

class YuGiRepo(
    private val api: YuGiAPIInterface,
    private val appContext: Application
): YuGiRepoInterface {

    init {
        val appName = appContext.getString(R.string.app_name)
        println("Hello from the repository $appName")
    }

    override suspend fun getSmallPlayingCards(query: String): List<SmallPlayingCard> {
        TODO("Not yet implemented")
    }

    override suspend fun getLargePlayingCards(id: String): List<LargePlayingCard> {
        TODO("Not yet implemented")
    }
}