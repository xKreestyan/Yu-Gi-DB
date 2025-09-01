package com.example.yu_gi_db.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yu_gi_db.data.local.db.entities.CardEntity
import com.example.yu_gi_db.data.local.db.entities.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entities.SetEntity
import com.example.yu_gi_db.data.local.db.entities.TypeLineEntity
import com.example.yu_gi_db.data.local.db.entities.CardTypeLineCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface YuGiDAO {

    // --- Insert Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

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

    @Query("SELECT * FROM sets WHERE name = :setName")
    suspend fun getSetByName(setName: String): SetEntity?

    @Query("SELECT * FROM sets WHERE id = :setId")
    suspend fun getSetById(setId: Long): SetEntity?

    @Query("SELECT * FROM type_lines WHERE name = :typeLineName")
    suspend fun getTypeLineByName(typeLineName: String): TypeLineEntity?

    @Query("""
        SELECT cards.*
        FROM cards
        INNER JOIN card_set_appearances ON cards.id = card_set_appearances.cardId
        INNER JOIN sets ON card_set_appearances.setId = sets.id
        WHERE sets.name = :setName
    """)
    suspend fun getCardsFromSetName(setName: String): List<CardEntity>

    @Query("""
        SELECT cards.*
        FROM cards
        INNER JOIN card_type_line_cross_ref ON cards.id = card_type_line_cross_ref.cardId
        INNER JOIN type_lines ON card_type_line_cross_ref.typeLineId = type_lines.id
        WHERE type_lines.name = :typeLineName
    """)
    suspend fun getCardsByTypeLineName(typeLineName: String): List<CardEntity>

    @Query("""
        SELECT tl.name 
        FROM card_type_line_cross_ref AS ctlcr
        INNER JOIN type_lines AS tl ON ctlcr.typeLineId = tl.id
        WHERE ctlcr.cardId = :cardId
    """)
    suspend fun getTypeLineNamesForCard(cardId: Int): List<String>

    @Query("SELECT * FROM card_set_appearances WHERE cardId = :cardId")
    suspend fun getAppearancesForCard(cardId: Int): List<CardSetAppearanceEntity>

    @Query("SELECT * FROM cards ORDER BY name ASC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM sets ORDER BY name ASC")
    fun getAllSets(): Flow<List<SetEntity>>

    @Query("SELECT * FROM type_lines ORDER BY name ASC")
    fun getAllTypeLines(): Flow<List<TypeLineEntity>>

    // --- NUOVE QUERY PER RICERCA AVANZATA NEL DATABASE LOCALE ---

    // Query su campi diretti di CardEntity

    @Query("SELECT * FROM cards WHERE name LIKE '%' || :nameQuery || '%'")
    fun getCardsByNameQuery(nameQuery: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE type = :type")
    fun getCardsByType(type: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE humanReadableCardType LIKE '%' || :hrTypeQuery || '%'")
    fun getCardsByHumanReadableType(hrTypeQuery: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE frameType = :frameType")
    fun getCardsByFrameType(frameType: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE `desc` LIKE '%' || :descQuery || '%'") // Corretto qui
    fun getCardsByDescription(descQuery: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE race = :race")
    fun getCardsByRace(race: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE level = :level")
    fun getCardsByLevel(level: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE atk = :atk")
    fun getCardsByAtk(atk: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE def = :def")
    fun getCardsByDef(def: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE attribute = :attributeName")
    fun getCardsByAttributeQuery(attributeName: String): Flow<List<CardEntity>>

    // Query su campi da tabelle correlate (richiedono JOIN)
    @Query("""
        SELECT DISTINCT cards.*
        FROM cards
        INNER JOIN card_type_line_cross_ref ON cards.id = card_type_line_cross_ref.cardId
        INNER JOIN type_lines ON card_type_line_cross_ref.typeLineId = type_lines.id
        WHERE type_lines.name LIKE '%' || :typeLineQuery || '%'
    """)
    fun getCardsByTypeLine(typeLineQuery: String): Flow<List<CardEntity>>

    @Query("""
        SELECT DISTINCT cards.*
        FROM cards
        INNER JOIN card_set_appearances ON cards.id = card_set_appearances.cardId
        INNER JOIN sets ON card_set_appearances.setId = sets.id
        WHERE sets.name LIKE '%' || :setNameQuery || '%'
    """)
    fun getCardsBySetNameQuery(setNameQuery: String): Flow<List<CardEntity>>

    @Query("""
        SELECT DISTINCT cards.*
        FROM cards
        INNER JOIN card_set_appearances ON cards.id = card_set_appearances.cardId
        WHERE card_set_appearances.rarity LIKE '%' || :rarityQuery || '%'
    """)
    fun getCardsBySetRarity(rarityQuery: String): Flow<List<CardEntity>>

    @Query("""
        SELECT DISTINCT cards.*
        FROM cards
        INNER JOIN card_set_appearances ON cards.id = card_set_appearances.cardId
        WHERE card_set_appearances.setSpecificCode LIKE '%' || :setCodeQuery || '%'
    """)
    fun getCardsBySetCode(setCodeQuery: String): Flow<List<CardEntity>>
}
