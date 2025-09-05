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
import kotlinx.coroutines.awaitAll // NUOVO IMPORT
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
        Log.d(tag, "fetchAndSaveAllCards called - GOAT Format") // Modificato il log
        try {
            // 1. Fetch delle carte del formato GOAT
            val goatCardsResponse = apiClient.fetchCards(mapOf("format" to "goat"))
            val goatCards = goatCardsResponse?.data ?: emptyList()

            // 2. Rimuovi la logica di unione di stapleCards e lobCards
            //    Ora usiamo direttamente goatCards come la nostra lista unica.
            if (goatCards.isEmpty()) {
                Log.d(tag, "No cards fetched from API for GOAT format or response was null/empty.")
                return
            }
            Log.d(tag, "Processing ${goatCards.size} cards from GOAT format.")

            // 3. Itera e processa le carte GOAT
            goatCards.forEachIndexed { index, apiCard ->
                Log.d(tag, "Processing card ${index + 1}/${goatCards.size}: ${apiCard.name} (ID: ${apiCard.id})")

                val imageUrlSmallApi = apiCard.cardImages.firstOrNull()?.imageUrlSmall
                var localImagePath: String? = null

                if (!imageUrlSmallApi.isNullOrBlank()) {
                    localImagePath = downloadAndSaveImageVolley(imageUrlSmallApi, apiCard.id, imageDir)
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
            Log.i(tag, "Successfully processed and saved ${goatCards.size} cards for GOAT format.")
        } catch (e: Exception) {
            Log.e(tag, "Error during fetchAndSaveAllCards (GOAT Format)", e) // Modificato il log
        }
    }

    override fun getSmallCardsStream(query: String?): Flow<List<SmallPlayingCard>> {
        Log.d(tag, "getSmallCardsStream called with query: $query")
        // TODO: Implement actual query filtering if 'query' parameter is to be used
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

    // --- NUOVA FUNZIONE HELPER PER MAPPARE CardEntity a LargePlayingCard ---
    private suspend fun mapCardEntityToLargePlayingCard(entity: CardEntity): LargePlayingCard = withContext(Dispatchers.IO) {
        val typelines = yuGiDAO.getTypeLineNamesForCard(entity.id)
        val cardImagesDomain = mutableListOf<com.example.yu_gi_db.model.CardImage>()
        if (entity.localImageSmallPath != null) {
            cardImagesDomain.add(com.example.yu_gi_db.model.CardImage(
                id = entity.id,
                imageUrl = "", // Non abbiamo l'URL originale completo qui, solo il percorso locale
                imageUrlSmall = entity.localImageSmallPath,
                imageUrlCropped = "" // Non abbiamo l'URL cropped qui
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
