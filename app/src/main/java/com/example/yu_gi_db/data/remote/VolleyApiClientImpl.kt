package com.example.yu_gi_db.data.remote

import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.example.yu_gi_db.model.LargePlayingCardResponse
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class VolleyApiClientImpl @Inject constructor(
    private val requestQueue: RequestQueue,
    private val gson: Gson
) : ApiClient {

    private val tag = "VolleyApiClient"
    private val baseUrl = "https://db.ygoprodeck.com/api/v7/cardinfo.php" // URL Base

    override suspend fun fetchCards(params: Map<String, String>): LargePlayingCardResponse? {
        // Costruisci l'URL con i parametri
        val uriBuilder = Uri.parse(baseUrl).buildUpon()
        params.forEach { (key, value) ->
            uriBuilder.appendQueryParameter(key, value)
        }
        val apiUrlWithParams = uriBuilder.build().toString()

        return suspendCancellableCoroutine { continuation ->
            Log.d(tag, "Attempting to fetch cards from API: $apiUrlWithParams")

            val stringRequest = object : StringRequest(
                Request.Method.GET,
                apiUrlWithParams, // Usa l'URL con i parametri
                {
                    responseString ->
                    Log.d(tag, "API Response successful for $apiUrlWithParams. Length: ${responseString.length}")
                    try {
                        val parsedResponse = gson.fromJson(responseString, LargePlayingCardResponse::class.java)
                        if (continuation.isActive) {
                            continuation.resume(parsedResponse)
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing JSON response from $apiUrlWithParams", e)
                        if (continuation.isActive) {
                            continuation.resumeWithException(e)
                        }
                    }
                },
                {
                    volleyError ->
                    Log.e(tag, "Volley API Error for $apiUrlWithParams", volleyError)
                    if (continuation.isActive) {
                        continuation.resumeWithException(volleyError)
                    }
                }
            ) {
                // Eventuali header personalizzati possono essere aggiunti qui
            }

            requestQueue.add(stringRequest)

            continuation.invokeOnCancellation {
                stringRequest.cancel()
                Log.d(tag, "API request cancelled for $apiUrlWithParams")
            }
        }
    }
}
