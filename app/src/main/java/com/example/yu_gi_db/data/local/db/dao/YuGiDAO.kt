package com.example.yu_gi_db.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.yu_gi_db.data.local.db.entities.CardEntity
import com.example.yu_gi_db.data.local.db.entities.CardLocalizationEntity // NUOVO IMPORT
import com.example.yu_gi_db.data.local.db.entities.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entities.SetEntity
import com.example.yu_gi_db.data.local.db.entities.TypeLineEntity
import com.example.yu_gi_db.data.local.db.entities.CardTypeLineCrossRef
import com.example.yu_gi_db.model.SmallPlayingCard // Assicurati che SmallPlayingCard abbia 'name'
import kotlinx.coroutines.flow.Flow

@Dao
interface YuGiDAO {

    // --- Insert Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity) // Inserirà solo i dati base della carta

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Per inserire/aggiornare le localizzazioni
    suspend fun insertLocalization(localization: CardLocalizationEntity) // NUOVO METODO

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSet(set: SetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardSetAppearance(appearance: CardSetAppearanceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTypeLine(typeLine: TypeLineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardTypeLineCrossRef(crossRef: CardTypeLineCrossRef)

    // --- Query Operations ---

    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): CardEntity?

    // NUOVO METODO per ottenere la localizzazione specifica
    @Query("SELECT * FROM card_localizations WHERE cardId = :cardId AND languageCode = :languageCode")
    suspend fun getLocalization(cardId: Int, languageCode: String): CardLocalizationEntity?

    @Query("SELECT * FROM sets WHERE name = :setName")
    suspend fun getSetByName(setName: String): SetEntity?

    @Query("SELECT * FROM sets WHERE id = :setId")
    suspend fun getSetById(setId: Long): SetEntity?

    @Query("SELECT * FROM type_lines WHERE name = :typeLineName")
    suspend fun getTypeLineByName(typeLineName: String): TypeLineEntity?

    // Modificata per includere JOIN con localizzazioni e languageCode
    // Assumendo che SmallPlayingCard abbia almeno: id, name, imageUrlSmall, isFavorite
    @Query("""
        SELECT DISTINCT c.id, cl.name, c.localImagePath AS imageUrlSmall, c.isFavorite
        FROM cards AS c
        INNER JOIN card_set_appearances AS csa ON c.id = csa.cardId
        INNER JOIN sets AS s ON csa.setId = s.id
        INNER JOIN card_localizations AS cl ON c.id = cl.cardId
        WHERE s.name = :setName AND cl.languageCode = :languageCode
    """
    )
    fun getInitialSmallCardsBySetName(setName: String, languageCode: String): Flow<List<SmallPlayingCard>>

    @Query("""
        SELECT tl.name 
        FROM card_type_line_cross_ref AS ctlcr
        INNER JOIN type_lines AS tl ON ctlcr.typeLineId = tl.id
        WHERE ctlcr.cardId = :cardId
    """
    )
    suspend fun getTypeLineNamesForCard(cardId: Int): List<String>

    @Query("SELECT * FROM card_set_appearances WHERE cardId = :cardId")
    suspend fun getAppearancesForCard(cardId: Int): List<CardSetAppearanceEntity>

    // Modificato per ordinare per nome localizzato e restituire SmallPlayingCard
    @Query("""
        SELECT c.id, cl.name, c.localImagePath AS imageUrlSmall, c.isFavorite
        FROM cards AS c
        INNER JOIN card_localizations AS cl ON c.id = cl.cardId
        WHERE cl.languageCode = :languageCode
        ORDER BY cl.name ASC
    """
    )
    fun getAllCards(languageCode: String): Flow<List<SmallPlayingCard>> // Firma e tipo di ritorno modificati

    @Query("SELECT * FROM sets ORDER BY name ASC")
    fun getAllSets(): Flow<List<SetEntity>>

    @Query("SELECT * FROM type_lines ORDER BY name ASC")
    fun getAllTypeLines(): Flow<List<TypeLineEntity>>

    // La query grezza costruita in YuGiRepo dovrà includere il JOIN con card_localizations
    // e selezionare i campi localizzati (es. name) per popolare SmallPlayingCard.
    @RawQuery(observedEntities = [CardEntity::class, CardLocalizationEntity::class])
    fun searchSmallCards(query: SupportSQLiteQuery): Flow<List<SmallPlayingCard>>

    // --- Favorite Card Operations ---

    @Query("UPDATE cards SET isFavorite = :isFavorite WHERE id = :cardId")
    suspend fun setFavoriteStatus(cardId: Int, isFavorite: Boolean)

    // Modificata per includere JOIN con localizzazioni e languageCode
    // Assumendo che SmallPlayingCard abbia almeno: id, name, imageUrlSmall, isFavorite
    @Query("""
        SELECT c.id, cl.name, c.localImagePath AS imageUrlSmall, c.isFavorite 
        FROM cards AS c
        INNER JOIN card_localizations AS cl ON c.id = cl.cardId
        WHERE c.isFavorite = 1 AND cl.languageCode = :languageCode
    """
    )
    fun getFavoriteSmallCards(languageCode: String): Flow<List<SmallPlayingCard>>
}
