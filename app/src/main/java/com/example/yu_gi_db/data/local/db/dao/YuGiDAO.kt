package com.example.yu_gi_db.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yu_gi_db.data.local.db.entity.CardEntity
import com.example.yu_gi_db.data.local.db.entity.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entity.SetEntity
import com.example.yu_gi_db.data.local.db.entity.TypeLineEntity // NUOVO IMPORT
import com.example.yu_gi_db.data.local.db.entity.CardTypeLineCrossRef // NUOVO IMPORT
import kotlinx.coroutines.flow.Flow

@Dao
interface YuGiDAO {

    // --- Insert Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSet(set: SetEntity): Long // Ritorna l'ID del set inserito o esistente

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardSetAppearance(appearance: CardSetAppearanceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignora se la typeline esiste già
    suspend fun insertTypeLine(typeLine: TypeLineEntity): Long // Ritorna l'ID

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Sostituisce se l'associazione esiste già
    suspend fun insertCardTypeLineCrossRef(crossRef: CardTypeLineCrossRef)

    // --- Query Operations ---

    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): CardEntity?

    @Query("SELECT * FROM sets WHERE name = :setName")
    suspend fun getSetByName(setName: String): SetEntity?

    @Query("SELECT * FROM sets WHERE id = :setId")
    suspend fun getSetById(setId: Long): SetEntity?

    @Query("SELECT * FROM type_lines WHERE name = :typeLineName") // NUOVA QUERY
    suspend fun getTypeLineByName(typeLineName: String): TypeLineEntity?

    /**
     * Recupera tutte le carte appartenenti a un set specifico, dato il nome del set.
     */
    @Query("""
        SELECT cards.*
        FROM cards
        INNER JOIN card_set_appearances ON cards.id = card_set_appearances.cardId
        INNER JOIN sets ON card_set_appearances.setId = sets.id
        WHERE sets.name = :setName
    """)
    suspend fun getCardsFromSetName(setName: String): List<CardEntity>

    /**
     * Recupera tutte le carte che hanno una specifica typeline, dato il nome della typeline.
     */
    @Query("""
        SELECT cards.*
        FROM cards
        INNER JOIN card_type_line_cross_ref ON cards.id = card_type_line_cross_ref.cardId
        INNER JOIN type_lines ON card_type_line_cross_ref.typeLineId = type_lines.id
        WHERE type_lines.name = :typeLineName
    """) // NUOVA QUERY
    suspend fun getCardsByTypeLineName(typeLineName: String): List<CardEntity>

    /**
     * Recupera i nomi di tutte le typeline associate a una specifica carta.
     */
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

    // Potrebbe essere utile anche una query per avere tutte le TypeLines, simile a getAllSets
    @Query("SELECT * FROM type_lines ORDER BY name ASC") // NUOVA QUERY OPZIONALE
    fun getAllTypeLines(): Flow<List<TypeLineEntity>>

}
