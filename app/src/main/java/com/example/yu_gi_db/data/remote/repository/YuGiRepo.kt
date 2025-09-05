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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
            val filename = "${cardId}.jpg" // MODIFICATO: Rimosso "_small"
            val localFile = File(imageSubDir, filename)

            if (localFile.exists()) {
                Log.d(tag, "Image already exists: ${localFile.absolutePath}")
                if (continuation.isActive) continuation.resume(localFile.absolutePath)
                return@suspendCancellableCoroutine
            }

            Log.d(tag, "Downloading image from: $imageUrl to ${localFile.absolutePath}")
            val imageRequest = ImageRequest(
                imageUrl,
                { bitmap ->
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
                { volleyError ->
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
        Log.d(tag, "fetchAndSaveAllCards called - Processing multiple sets")
        try {
            val setNames = listOf(
                "Legend of Blue Eyes White Dragon",
                "Metal Raiders",
                "Spell Ruler",
                "Pharaoh's Servant",
                "Labyrinth of Nightmare",
                "Legacy of Darkness"
            )

            val cardResponses = coroutineScope {
                setNames.map { setName ->
                    async(Dispatchers.IO) {
                        Log.d(tag, "Fetching cards for set: $setName")
                        apiClient.fetchCards(mapOf("cardset" to setName))
                    }
                }.awaitAll()
            }

            val allCardsFromApi = mutableListOf<LargePlayingCard>()
            cardResponses.forEachIndexed { index, response ->
                val setName = setNames[index]
                if (response?.data != null && response.data.isNotEmpty()) {
                    Log.d(tag, "Successfully fetched ${response.data.size} cards for set: $setName")
                    allCardsFromApi.addAll(response.data)
                } else {
                    Log.w(tag, "No cards fetched or empty data for set: $setName. Response: $response")
                }
            }

            if (allCardsFromApi.isEmpty()) {
                Log.d(tag, "No cards fetched from any API set or all responses were null/empty.")
                return
            }

            val uniqueCardsMap = mutableMapOf<Int, LargePlayingCard>()
            allCardsFromApi.forEach { apiCard ->
                uniqueCardsMap[apiCard.id] = apiCard
            }
            val cardsToProcess = uniqueCardsMap.values.toList()

            Log.d(tag, "Total unique cards to process after merging ${setNames.size} sets: ${cardsToProcess.size}")

            cardsToProcess.forEachIndexed { index, apiCard ->
                Log.d(tag, "Processing card ${index + 1}/${cardsToProcess.size}: ${apiCard.name} (ID: ${apiCard.id})")

                // MODIFICATO: Usa l'URL dell'immagine grande
                val imageUrlApi = apiCard.cardImages.firstOrNull()?.imageUrl
                var localImagePathResult: String? = null // Rinominata variabile per chiarezza

                if (!imageUrlApi.isNullOrBlank()) {
                    localImagePathResult = downloadAndSaveImageVolley(imageUrlApi, apiCard.id, imageDir)
                } else {
                    Log.w(tag, "No large image URL found for card ID ${apiCard.id}") // Log aggiornato
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
                    localImagePath = localImagePathResult, // MODIFICATO: Usa il campo rinominato
                    cardPrices = apiCard.cardPrices
                )
                yuGiDAO.insertCard(cardEntity)

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
                    if (setNames.contains(apiSet.setName)) {
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
            }
            Log.i(tag, "Successfully processed and saved ${cardsToProcess.size} unique cards from multiple sets.")
        } catch (e: Exception) {
            Log.e(tag, "Error during fetchAndSaveAllCards from multiple sets", e)
        }
    }

    override fun getSmallCardsStream(query: String?): Flow<List<SmallPlayingCard>> {
        Log.d(tag, "getSmallCardsStream called with query: $query")
        return yuGiDAO.getAllCards().map { entities ->
            Log.d(tag, "Mapping ${entities.size} CardEntities to SmallPlayingCards")
            entities.map { entity ->
                SmallPlayingCard(
                    id = entity.id,
                    // MODIFICATO: Usa il campo rinominato. Il campo in SmallPlayingCard rimane imageUrlSmall
                    // per coerenza con l'uso (mostrare un'immagine), ma ora punta all'immagine grande.
                    imageUrlSmall = entity.localImagePath ?: ""
                )
            }
        }
    }

    private suspend fun mapCardEntityToLargePlayingCard(entity: CardEntity): LargePlayingCard = withContext(Dispatchers.IO) {
        val typelines = yuGiDAO.getTypeLineNamesForCard(entity.id)
        val cardImagesDomain = mutableListOf<com.example.yu_gi_db.model.CardImage>()

        // MODIFICATO: Usa il campo rinominato
        if (entity.localImagePath != null) {
            cardImagesDomain.add(com.example.yu_gi_db.model.CardImage(
                id = entity.id,
                imageUrl = "", // L'URL originale completo non è memorizzato, solo il percorso locale
                // MODIFICATO: Il campo in CardImage model rimane imageUrlSmall, ma ora punta all'immagine grande.
                imageUrlSmall = entity.localImagePath,
                imageUrlCropped = "" // L'URL cropped non è memorizzato
            ))
        }

        val setAppearances = yuGiDAO.getAppearancesForCard(entity.id)
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

        LargePlayingCard(
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

    override suspend fun getLargeCardById(cardId: Int): LargePlayingCard? = withContext(Dispatchers.IO) {
        Log.d(tag, "getLargeCardById called for ID: $cardId")
        val entity = yuGiDAO.getCardById(cardId)
        if (entity == null) {
            Log.w(tag, "No CardEntity found for ID: $cardId")
            return@withContext null
        }
        mapCardEntityToLargePlayingCard(entity)
    }

    // --- IMPLEMENTAZIONE DELLE NUOVE FUNZIONI DI RICERCA ---
    // (Queste funzioni non necessitano di modifiche dirette qui perché
    // si basano su mapCardEntityToLargePlayingCard, che è già stato aggiornato)

    override fun getCardsByName(cardNameQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByNameQuery(cardNameQuery).map { entities ->
            coroutineScope { // o supervisorScope se le mappature possono fallire individualmente
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByAttribute(attributeName: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByAttributeQuery(attributeName).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByType(type: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByType(type).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

     override fun getCardsByHumanReadableType(hrTypeQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByHumanReadableType(hrTypeQuery).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByFrameType(frameType: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByFrameType(frameType).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByDescription(descQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByDescription(descQuery).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByRace(race: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByRace(race).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByLevel(level: Int): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByLevel(level).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByAtk(atk: Int): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByAtk(atk).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByDef(def: Int): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByDef(def).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsByTypeLine(typeLineQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsByTypeLine(typeLineQuery).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsBySetName(setNameQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsBySetNameQuery(setNameQuery).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsBySetRarity(rarityQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsBySetRarity(rarityQuery).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }

    override fun getCardsBySetCode(setCodeQuery: String): Flow<List<LargePlayingCard>> {
        return yuGiDAO.getCardsBySetCode(setCodeQuery).map { entities ->
            coroutineScope {
                entities.map { entity ->
                    async(Dispatchers.IO) { mapCardEntityToLargePlayingCard(entity) }
                }.awaitAll()
            }
        }
    }
}
