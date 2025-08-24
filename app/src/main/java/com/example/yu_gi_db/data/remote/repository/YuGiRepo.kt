package com.example.yu_gi_db.data.remote.repository

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.example.yu_gi_db.data.local.db.dao.YuGiDAO
import com.example.yu_gi_db.data.local.db.entities.CardEntity
import com.example.yu_gi_db.data.local.db.entities.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entities.CardTypeLineCrossRef
import com.example.yu_gi_db.data.local.db.entities.SetEntity
import com.example.yu_gi_db.data.local.db.entities.TypeLineEntity
import com.example.yu_gi_db.data.remote.ApiClient
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async // ASSICURARSI CHE QUESTO IMPORT SIA PRESENTE O AGGIUNTO
import kotlinx.coroutines.coroutineScope // ASSICURARSI CHE QUESTO IMPORT SIA PRESENTE O AGGIUNTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume

class YuGiRepo @Inject constructor(
    private val appContext: Application,
    private val yuGiDAO: YuGiDAO,
    private val apiClient: ApiClient,
    private val imageRequestQueue: RequestQueue
) : YuGiRepoInterface {

    private val imageDir = File(appContext.filesDir, "card_images")
    private val tag = "YuGiRepo"

    init {
        println("YuGiRepo initialized. DAO: $yuGiDAO, ApiClient: $apiClient, RequestQueue: $imageRequestQueue")
        imageDir.mkdirs()
    }

    private suspend fun downloadAndSaveImageVolley(
        imageUrl: String,
        cardId: Int,
        imageSubDir: File
    ): String? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<String?> { continuation ->
            val filename = "${cardId}_small.jpg"
            val localFile = File(imageSubDir, filename)

            if (localFile.exists()) {
                Log.d(tag, "Image already exists: ${localFile.absolutePath}")
                if (continuation.isActive) continuation.resume(localFile.absolutePath)
                return@suspendCancellableCoroutine
            }

            Log.d(tag, "Downloading image from: $imageUrl to ${localFile.absolutePath}")
            val imageRequest = ImageRequest(
                imageUrl,
                {
                        bitmap ->
                    try {
                        imageSubDir.mkdirs()
                        FileOutputStream(localFile).use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                        }
                        Log.d(tag, "Image saved: ${localFile.absolutePath}")
                        if (continuation.isActive) continuation.resume(localFile.absolutePath)
                    } catch (e: Exception) {
                        Log.e(tag, "Error saving image ${localFile.absolutePath}", e)
                        localFile.delete()
                        if (continuation.isActive) continuation.resume(null)
                    }
                },
                0, 0,
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                {
                        volleyError ->
                    Log.e(tag, "Volley error downloading image $imageUrl", volleyError)
                    if (continuation.isActive) continuation.resume(null)
                }
            )
            imageRequestQueue.add(imageRequest)
            continuation.invokeOnCancellation {
                imageRequest.cancel()
                Log.d(tag, "Image request cancelled for $imageUrl")
            }
        }
    }

    override suspend fun fetchAndSaveAllCards() {
        Log.d(tag, "fetchAndSaveAllCards called")
        try {
            // --- INIZIO SEZIONE MODIFICATA ---
            // Esegui le chiamate API in parallelo
            val (stapleCardsResponse, lobCardsResponse) = coroutineScope {
                // Esegui le chiamate API su Dispatchers.IO se sono blocking o se l'ApiClient non lo fa già
                val stapleCardsDeferred = async(Dispatchers.IO) { apiClient.fetchCards(mapOf("staple" to "yes")) }
                val lobCardsDeferred = async(Dispatchers.IO) { apiClient.fetchCards(mapOf("cardset" to "Legend of Blue Eyes White Dragon")) }
                // Attendere il completamento di entrambe le chiamate
                Pair(stapleCardsDeferred.await(), lobCardsDeferred.await())
            }

            val stapleCards = stapleCardsResponse?.data ?: emptyList()
            Log.d(tag, "Fetched ${stapleCards.size} staple cards from API.")

            val lobCards = lobCardsResponse?.data ?: emptyList()
            Log.d(tag, "Fetched ${lobCards.size} LOB cards from API.")

            // Unisci le liste e rimuovi i duplicati basati sull'ID della carta
            val allCardsMap = mutableMapOf<Int, LargePlayingCard>()
            stapleCards.forEach { allCardsMap[it.id] = it }
            lobCards.forEach { allCardsMap[it.id] = it } // Sovrascrive i duplicati, mantenendo l'ultima versione

            val uniqueCards = allCardsMap.values.toList()

            if (uniqueCards.isEmpty()) {
                Log.d(tag, "No unique cards fetched from API or responses were null/empty.")
                return
            }
            Log.d(tag, "Processing ${uniqueCards.size} unique cards in total.")

            uniqueCards.forEachIndexed { index, apiCard ->
                // --- FINE SEZIONE MODIFICATA ---
                Log.d(tag, "Processing card ${index + 1}/${uniqueCards.size}: ${apiCard.name} (ID: ${apiCard.id})")

                val imageUrlSmallApi = apiCard.cardImages.firstOrNull()?.imageUrlSmall
                var localImagePath: String? = null

                if (!imageUrlSmallApi.isNullOrBlank()) {
                    localImagePath = downloadAndSaveImageVolley(imageUrlSmallApi, apiCard.id, imageDir)
                    if (localImagePath != null) {
                        Log.d(tag, "Image for card ID ${apiCard.id} downloaded to: $localImagePath")
                    } else {
                        Log.w(tag, "Failed to download image for card ID ${apiCard.id} from $imageUrlSmallApi")
                    }
                } else {
                    Log.w(tag, "No small image URL found for card ID ${apiCard.id}")
                }

                val cardEntity = CardEntity(
                    id = apiCard.id,
                    name = apiCard.name,
                    type = apiCard.type,
                    humanReadableCardType = apiCard.humanReadableCardType,
                    frameType = apiCard.frameType,
                    desc = apiCard.desc,
                    race = apiCard.race,
                    atk = apiCard.atk,
                    def = apiCard.def,
                    level = apiCard.level,
                    attribute = apiCard.attribute,
                    localImageSmallPath = localImagePath,
                    cardPrices = apiCard.cardPrices
                )
                yuGiDAO.insertCard(cardEntity)
                Log.d(tag, "Inserted/Updated CardEntity for ID: ${apiCard.id}")

                apiCard.typeline?.forEach { typeLineName ->
                    if (typeLineName.isNotBlank()) {
                        var typeLineEntity = yuGiDAO.getTypeLineByName(typeLineName)
                        val typeLineId: Long
                        if (typeLineEntity == null) {
                            typeLineId = yuGiDAO.insertTypeLine(TypeLineEntity(name = typeLineName))
                        } else {
                            typeLineId = typeLineEntity.id
                        }
                        yuGiDAO.insertCardTypeLineCrossRef(CardTypeLineCrossRef(cardId = apiCard.id, typeLineId = typeLineId))
                    }
                }

                apiCard.cardSets?.forEach { apiSet ->
                    var setEntity = yuGiDAO.getSetByName(apiSet.setName)
                    val setId: Long
                    if (setEntity == null) {
                        setId = yuGiDAO.insertSet(SetEntity(name = apiSet.setName))
                    } else {
                        setId = setEntity.id
                    }
                    val appearance = CardSetAppearanceEntity(
                        cardId = apiCard.id,
                        setId = setId,
                        setSpecificCode = apiSet.setCode,
                        rarity = apiSet.setRarity,
                        rarityCode = apiSet.setRarityCode ?: "",
                        price = apiSet.setPrice
                    )
                    yuGiDAO.insertCardSetAppearance(appearance)
                }
            }
            // --- INIZIO SEZIONE MODIFICATA (SOLO LA VARIABILE NEL LOG) ---
            Log.i(tag, "Successfully processed and saved ${uniqueCards.size} cards.")
            // --- FINE SEZIONE MODIFICATA ---
        } catch (e: Exception) {
            Log.e(tag, "Error during fetchAndSaveAllCards", e)
        }
    }

    override fun getSmallCardsStream(query: String?): Flow<List<SmallPlayingCard>> {
        Log.d(tag, "getSmallCardsStream called with query: $query")
        return yuGiDAO.getAllCards().map { entities ->
            Log.d(tag, "Mapping ${entities.size} CardEntities to SmallPlayingCards")
            entities.map { entity ->
                SmallPlayingCard(
                    id = entity.id,
                    imageUrlSmall = entity.localImageSmallPath ?: ""
                )
            }
        }
    }

    override suspend fun getLargeCardById(cardId: Int): LargePlayingCard? = withContext(Dispatchers.IO) {
        Log.d(tag, "getLargeCardById called for ID: $cardId")
        val entity = yuGiDAO.getCardById(cardId)
        if (entity == null) {
            Log.w(tag, "No CardEntity found for ID: $cardId")
            return@withContext null
        }

        Log.d(tag, "Found CardEntity: ${entity.name}")
        val typelines = yuGiDAO.getTypeLineNamesForCard(cardId)
        Log.d(tag, "Typelines for card ID $cardId: $typelines")

        val cardImagesDomain = mutableListOf<com.example.yu_gi_db.model.CardImage>()
        if (entity.localImageSmallPath != null) {
            cardImagesDomain.add(com.example.yu_gi_db.model.CardImage(
                id = entity.id,
                imageUrl = "",
                imageUrlSmall = entity.localImageSmallPath,
                imageUrlCropped = ""
            ))
        }
        Log.d(tag, "Constructed cardImages for LargePlayingCard: $cardImagesDomain")

        val setAppearances = yuGiDAO.getAppearancesForCard(cardId)
        val cardSetsDomain = mutableListOf<com.example.yu_gi_db.model.CardSet>()
        setAppearances.forEach { appearance ->
            val setEntity = yuGiDAO.getSetById(appearance.setId)
            if (setEntity != null) {
                cardSetsDomain.add(com.example.yu_gi_db.model.CardSet(
                    setName = setEntity.name,
                    setCode = appearance.setSpecificCode,
                    setRarity = appearance.rarity,
                    setRarityCode = appearance.rarityCode,
                    setPrice = appearance.price
                ))
            }
        }
        Log.d(tag, "Constructed cardSets for LargePlayingCard: $cardSetsDomain")

        return@withContext LargePlayingCard(
            id = entity.id,
            name = entity.name,
            typeline = typelines,
            type = entity.type,
            humanReadableCardType = entity.humanReadableCardType,
            frameType = entity.frameType,
            desc = entity.desc,
            race = entity.race,
            atk = entity.atk,
            def = entity.def,
            level = entity.level,
            attribute = entity.attribute,
            cardImages = cardImagesDomain,
            cardSets = cardSetsDomain,
            cardPrices = entity.cardPrices
        )
    }
}

