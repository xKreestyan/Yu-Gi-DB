package com.example.yu_gi_db.data.remote

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.LargePlayingCardResponse
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.model.SmallPlayingCardResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class YuGiAPI @Inject constructor(
    @ApplicationContext private val context: Context
) : YuGiAPIInterface {

    private val requestQueue = Volley.newRequestQueue(context)
    private val gson = Gson()

    override suspend fun getSmallPlayingCards(query: String): List<SmallPlayingCard> =
        suspendCancellableCoroutine { continuation ->
            val url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name=$query"

            val request = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val parsed = gson.fromJson(response, SmallPlayingCardResponse::class.java)
                        continuation.resume(parsed.cards ?: emptyList())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                },
                { error -> continuation.resumeWithException(error) }
            )

            requestQueue.add(request)
        }

    override suspend fun getLargePlayingCards(id: String): List<LargePlayingCard> =
        suspendCancellableCoroutine { continuation ->
            val url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?id=$id"

            val request = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val parsed = gson.fromJson(response, LargePlayingCardResponse::class.java)
                        continuation.resume(parsed.cards ?: emptyList())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                },
                { error -> continuation.resumeWithException(error) }
            )

            requestQueue.add(request)
        }
}
