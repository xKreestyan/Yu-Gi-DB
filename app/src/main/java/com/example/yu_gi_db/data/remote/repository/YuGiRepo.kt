package com.example.yu_gi_db.data.remote.repository

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.sqlite.db.SimpleSQLiteQuery
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.example.yu_gi_db.data.local.db.dao.YuGiDAO
import com.example.yu_gi_db.data.local.db.entities.CardEntity
import com.example.yu_gi_db.data.local.db.entities.CardLocalizationEntity // IMPORT AGGIUNTO
import com.example.yu_gi_db.data.local.db.entities.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entities.CardTypeLineCrossRef
import com.example.yu_gi_db.data.local.db.entities.SetEntity
import com.example.yu_gi_db.data.local.db.entities.TypeLineEntity
import com.example.yu_gi_db.data.remote.ApiClient
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
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
    private val defaultSetName = "Legend of Blue Eyes White Dragon"

    init {
        println("YuGiRepo initialized. DAO: $yuGiDAO, ApiClient: $apiClient, RequestQueue: $imageRequestQueue")
        imageDir.mkdirs()
    }

    private fun getCurrentLanguageParam(): String {
        val deviceLanguage = Locale.getDefault().language
        val supportedLanguages = listOf("it", "en")
        return if (deviceLanguage in supportedLanguages) deviceLanguage else "en"
    }

    private suspend fun downloadAndSaveImageVolley(
        imageUrl: String,
        cardId: Int,
        imageSubDir: File
    ): String? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<String?> { continuation ->
            val filename = "${cardId}.jpg"
            val localFile = File(imageSubDir, filename)

            if (localFile.exists()) {
                if (continuation.isActive) continuation.resume(localFile.absolutePath)
                return@suspendCancellableCoroutine
            }

            val imageRequest = ImageRequest(
                imageUrl,
                { bitmap ->
                    try {
                        imageSubDir.mkdirs()
                        FileOutputStream(localFile).use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                        }
                        if (continuation.isActive) continuation.resume(localFile.absolutePath)
                    } catch (e: Exception) {
                        localFile.delete()
                        if (continuation.isActive) continuation.resume(null)
                    }
                },
                0, 0,
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                { volleyError ->
                    if (continuation.isActive) continuation.resume(null)
                }
            )
            imageRequestQueue.add(imageRequest)
            continuation.invokeOnCancellation { imageRequest.cancel() }
        }
    }

    override suspend fun fetchAndSaveAllCards() {
        Log.d(tag, "fetchAndSaveAllCards called - Processing multiple sets")
        try {
            val languageParam = getCurrentLanguageParam() // Questo restituisce "it" o "en"
            Log.d(tag, "Using language code: $languageParam for fetching cards logic")

            val setNames = listOf(defaultSetName, "Metal Raiders")

            val cardResponses = coroutineScope {
                setNames.map { setName ->
                    async(Dispatchers.IO) {
                        // COSTRUZIONE DINAMICA DEI PARAMETRI API
                        val apiParams = mutableMapOf<String, String>()
                        apiParams["cardset"] = setName
                        if (languageParam != "en") { // Aggiungi il parametro lingua solo se NON è inglese
                            apiParams["language"] = languageParam
                            Log.d(tag, "Fetching cards for set: $setName with language: $languageParam")
                        } else {
                            Log.d(tag, "Fetching cards for set: $setName (defaulting to English, no language param)")
                        }
                        apiClient.fetchCards(apiParams) // Passa i parametri costruiti
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

            val uniqueCardsMap = allCardsFromApi.associateBy { it.id }
            val cardsToProcess = uniqueCardsMap.values.toList()

            Log.d(tag, "Total unique cards to process after merging sets: ${cardsToProcess.size}")

            cardsToProcess.forEachIndexed { index, apiCard ->
                Log.d(tag, "Processing card ${index + 1}/${cardsToProcess.size}: ${apiCard.name} (ID: ${apiCard.id})")

                val existingCardEntity = yuGiDAO.getCardById(apiCard.id)
                val currentIsFavoriteState = existingCardEntity?.isFavorite ?: false

                val imageUrlApi = apiCard.cardImages.firstOrNull()?.imageUrl
                var localImagePathResult: String? = null
                if (!imageUrlApi.isNullOrBlank()) {
                    localImagePathResult = downloadAndSaveImageVolley(imageUrlApi, apiCard.id, imageDir)
                }

                // Dati per CardEntity (non localizzati)
                val cardEntity = CardEntity(
                    id = apiCard.id,
                    type = apiCard.type, // Assumendo type sia cross-lingua o gestito diversamente
                    frameType = apiCard.frameType,
                    race = apiCard.race,
                    atk = apiCard.atk,
                    def = apiCard.def,
                    level = apiCard.level,
                    attribute = apiCard.attribute,
                    localImagePath = localImagePathResult,
                    cardPrices = apiCard.cardPrices,
                    isFavorite = currentIsFavoriteState
                )
                yuGiDAO.insertCard(cardEntity)

                // Dati per CardLocalizationEntity
                // La lingua usata per salvare la localizzazione è `languageParam`
                // che è la lingua rilevata o il fallback a 'en' se non è 'it'.
                // Se l'API restituisce dati inglesi quando languageParam è 'en' (senza &language=en),
                // allora salveremo correttamente i dati inglesi con il codice 'en'.
                // Se l'API restituisce dati italiani quando languageParam è 'it' (con &language=it),
                // allora salveremo correttamente i dati italiani con il codice 'it'.
                val localizationEntity = CardLocalizationEntity(
                    cardId = apiCard.id,
                    languageCode = languageParam, // Usa languageParam per coerenza nel DB
                    name = apiCard.name,
                    desc = apiCard.desc,
                    humanReadableCardType = apiCard.humanReadableCardType
                )
                yuGiDAO.insertLocalization(localizationEntity)

                apiCard.typeline?.forEach { typeLineName ->
                    if (typeLineName.isNotBlank()) {
                        var typeLineEntity = yuGiDAO.getTypeLineByName(typeLineName)
                        val typeLineId = typeLineEntity?.id ?: yuGiDAO.insertTypeLine(TypeLineEntity(name = typeLineName))
                        yuGiDAO.insertCardTypeLineCrossRef(CardTypeLineCrossRef(cardId = apiCard.id, typeLineId = typeLineId))
                    }
                }

                apiCard.cardSets?.forEach { apiSet ->
                    if (setNames.contains(apiSet.setName)) { // Assicurati di salvare solo i set richiesti
                        var setEntity = yuGiDAO.getSetByName(apiSet.setName)
                        val setId = setEntity?.id ?: yuGiDAO.insertSet(SetEntity(name = apiSet.setName))
                        val appearance = CardSetAppearanceEntity(
                            cardId = apiCard.id, setId = setId, setSpecificCode = apiSet.setCode,
                            rarity = apiSet.setRarity, rarityCode = apiSet.setRarityCode ?: "", price = apiSet.setPrice
                        )
                        yuGiDAO.insertCardSetAppearance(appearance)
                    }
                }
            }
            Log.i(tag, "Successfully processed and saved ${cardsToProcess.size} unique cards.")
        } catch (e: Exception) {
            Log.e(tag, "Error during fetchAndSaveAllCards", e)
        }
    }

    override fun getDefaultSetSmallCardsStream(): Flow<List<SmallPlayingCard>> {
        val languageParam = getCurrentLanguageParam()
        Log.d(tag, "getDefaultSetSmallCardsStream called for set: $defaultSetName with language: $languageParam")
        return yuGiDAO.getInitialSmallCardsBySetName(defaultSetName, languageParam)
    }

    // mapCardEntityToLargePlayingCard rinominata e adattata
    private suspend fun mapDetailsToLargePlayingCard(
        entity: CardEntity,
        localization: CardLocalizationEntity // Ora richiede CardLocalizationEntity
    ): LargePlayingCard = withContext(Dispatchers.IO) {
        val typelines = yuGiDAO.getTypeLineNamesForCard(entity.id)
        val cardImagesDomain = mutableListOf<com.example.yu_gi_db.model.CardImage>()

        if (entity.localImagePath != null) {
            cardImagesDomain.add(com.example.yu_gi_db.model.CardImage(
                id = entity.id, imageUrl = "", imageUrlSmall = entity.localImagePath, imageUrlCropped = ""
            ))
        }

        val setAppearances = yuGiDAO.getAppearancesForCard(entity.id)
        val cardSetsDomain = mutableListOf<com.example.yu_gi_db.model.CardSet>()
        setAppearances.forEach { appearance ->
            val setEntity = yuGiDAO.getSetById(appearance.setId)
            if (setEntity != null) {
                cardSetsDomain.add(com.example.yu_gi_db.model.CardSet(
                    setName = setEntity.name, setCode = appearance.setSpecificCode,
                    setRarity = appearance.rarity, setRarityCode = appearance.rarityCode, 
                    setPrice = appearance.price // CORREZIONE APPLICATA QUI
                ))
            }
        }

        return@withContext LargePlayingCard(
            id = entity.id,
            name = localization.name, // Da localization
            typeline = typelines,
            type = entity.type, // Da CardEntity
            humanReadableCardType = localization.humanReadableCardType, // Da localization
            frameType = entity.frameType,
            desc = localization.desc, // Da localization
            race = entity.race,
            atk = entity.atk,
            def = entity.def,
            level = entity.level,
            attribute = entity.attribute,
            cardImages = cardImagesDomain,
            cardSets = cardSetsDomain,
            cardPrices = entity.cardPrices,
            isFavorite = entity.isFavorite
        )
    }

    override suspend fun getLargeCardById(cardId: Int): LargePlayingCard? = withContext(Dispatchers.IO) {
        Log.d(tag, "getLargeCardById called for ID: $cardId")
        val entity = yuGiDAO.getCardById(cardId)
        if (entity == null) {
            Log.w(tag, "No CardEntity found for ID: $cardId")
            return@withContext null
        }

        var languageToFetch = getCurrentLanguageParam()
        var localization = yuGiDAO.getLocalization(cardId, languageToFetch)

        // Fallback a Inglese se la localizzazione primaria non è trovata E non era già inglese
        if (localization == null && languageToFetch != "en") {
            Log.w(tag, "No localization found for card ID $cardId in '$languageToFetch'. Trying 'en'.")
            localization = yuGiDAO.getLocalization(cardId, "en")
        }

        if (localization == null) {
            // Se anche il fallback fallisce, non possiamo costruire LargePlayingCard come definito (campi non nullable)
            Log.e(tag, "Critical: No localization found for card ID $cardId even after fallback to 'en'. Cannot create LargePlayingCard.")
            return@withContext null
        }

        return@withContext mapDetailsToLargePlayingCard(entity, localization)
    }

    override fun searchSmallCards(criteria: AdvancedSearchCriteria): Flow<List<SmallPlayingCard>> {
        val languageParam = getCurrentLanguageParam()
        Log.d(tag, "searchSmallCards called with criteria: $criteria for language: $languageParam")

        val queryBuilder = StringBuilder(
            "SELECT DISTINCT c.id, cl.name, c.localImagePath AS imageUrlSmall, c.isFavorite " +
            "FROM cards c " +
            "INNER JOIN card_localizations cl ON c.id = cl.cardId " +
            "WHERE cl.languageCode = ?" // Argomento 1: languageParam
        )
        val args = mutableListOf<Any>()
        args.add(languageParam)

        criteria.idQuery?.takeIf { it.isNotBlank() }?.let {
            queryBuilder.append(" AND CAST(c.id AS TEXT) LIKE ?")
            args.add("%$it%")
        }
        criteria.name?.takeIf { it.isNotBlank() }?.let {
            queryBuilder.append(" AND cl.name LIKE ?") // Cerca nel nome localizzato (cl.name)
            args.add("%$it%")
        }
        criteria.type?.takeIf { it.isNotBlank() }?.let {
            queryBuilder.append(" AND c.type = ?") // type da CardEntity
            args.add(it)
        }
        criteria.attribute?.takeIf { it.isNotBlank() }?.let {
            queryBuilder.append(" AND c.attribute = ?")
            args.add(it)
        }
        criteria.level?.let {
            queryBuilder.append(" AND c.level = ?")
            args.add(it)
        }
        criteria.atkMin?.let { queryBuilder.append(" AND c.atk >= ?"); args.add(it) }
        criteria.atkMax?.let { queryBuilder.append(" AND c.atk <= ?"); args.add(it) }
        criteria.defMin?.let { queryBuilder.append(" AND c.def >= ?"); args.add(it) }
        criteria.defMax?.let { queryBuilder.append(" AND c.def <= ?"); args.add(it) }
        
        criteria.isFavorite?.let {
            queryBuilder.append(" AND c.isFavorite = ?")
            args.add(if (it) 1 else 0)
        }

        val onlyIdQuery = criteria.idQuery?.isNotBlank() == true
        val otherCriteriaPresent = criteria.name?.isNotBlank() == true ||
                                   criteria.type?.isNotBlank() == true ||
                                   criteria.attribute?.isNotBlank() == true ||
                                   criteria.level != null ||
                                   criteria.atkMin != null || criteria.atkMax != null ||
                                   criteria.defMin != null || criteria.defMax != null ||
                                   criteria.isFavorite != null

        if (onlyIdQuery && !otherCriteriaPresent) {
            queryBuilder.append(" ORDER BY c.id ASC")
        } else {
            queryBuilder.append(" ORDER BY cl.name ASC") // Ordina per nome localizzato
        }

        val simpleSQLiteQuery = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        Log.d(tag, "Executing search query: ${simpleSQLiteQuery.sql} with args: ${args.joinToString()}")

        return yuGiDAO.searchSmallCards(simpleSQLiteQuery)
    }

    override suspend fun toggleFavoriteStatus(cardId: Int) {
        withContext(Dispatchers.IO) {
            val card = yuGiDAO.getCardById(cardId) // getCardById restituisce CardEntity
            if (card != null) {
                yuGiDAO.setFavoriteStatus(cardId, !card.isFavorite)
                Log.d(tag, "Toggled favorite status for card ID $cardId to ${!card.isFavorite}")
            } else {
                Log.w(tag, "Card not found with ID $cardId, cannot toggle favorite status.")
            }
        }
    }

    override fun getFavoriteSmallCardsStream(): Flow<List<SmallPlayingCard>> {
        val languageParam = getCurrentLanguageParam()
        Log.d(tag, "getFavoriteSmallCardsStream called for language: $languageParam")
        return yuGiDAO.getFavoriteSmallCards(languageParam)
    }
}
