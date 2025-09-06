package com.example.yu_gi_db.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery // AGGIUNTO
import androidx.sqlite.db.SupportSQLiteQuery // AGGIUNTO
import com.example.yu_gi_db.data.local.db.entities.CardEntity
import com.example.yu_gi_db.data.local.db.entities.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entities.SetEntity
import com.example.yu_gi_db.data.local.db.entities.TypeLineEntity
import com.example.yu_gi_db.data.local.db.entities.CardTypeLineCrossRef
import com.example.yu_gi_db.model.SmallPlayingCard // Assicurati che sia importato
import kotlinx.coroutines.flow.Flow

@Dao
interface YuGiDAO {

    // --- Insert Operations --- (INVARIATE)

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
    suspend fun getCardById(cardId: Int): CardEntity? // INVARIATA (per i dettagli)

    @Query("SELECT * FROM sets WHERE name = :setName")
    suspend fun getSetByName(setName: String): SetEntity? // INVARIATA

    @Query("SELECT * FROM sets WHERE id = :setId")
    suspend fun getSetById(setId: Long): SetEntity? // INVARIATA

    @Query("SELECT * FROM type_lines WHERE name = :typeLineName")
    suspend fun getTypeLineByName(typeLineName: String): TypeLineEntity? // INVARIATA

    // MODIFICATA/RINOMINATA: Per caricare SmallPlayingCard del set di default (es. LOB)
    @Query("""
        SELECT c.id, c.localImagePath AS imageUrlSmall
        FROM cards AS c
        INNER JOIN card_set_appearances AS csa ON c.id = csa.cardId
        INNER JOIN sets AS s ON csa.setId = s.id
        WHERE s.name = :setName
    """)
    fun getInitialSmallCardsBySetName(setName: String): Flow<List<SmallPlayingCard>>

    @Query("""
        SELECT tl.name 
        FROM card_type_line_cross_ref AS ctlcr
        INNER JOIN type_lines AS tl ON ctlcr.typeLineId = tl.id
        WHERE ctlcr.cardId = :cardId
    """)
    suspend fun getTypeLineNamesForCard(cardId: Int): List<String> // INVARIATA (per dettagli)

    @Query("SELECT * FROM card_set_appearances WHERE cardId = :cardId")
    suspend fun getAppearancesForCard(cardId: Int): List<CardSetAppearanceEntity> // INVARIATA (per dettagli)

    @Query("SELECT * FROM cards ORDER BY name ASC")
    fun getAllCards(): Flow<List<CardEntity>> // Mantenuta per ora, potrebbe non essere usata attivamente nel nuovo flusso

    @Query("SELECT * FROM sets ORDER BY name ASC")
    fun getAllSets(): Flow<List<SetEntity>> // INVARIATA

    @Query("SELECT * FROM type_lines ORDER BY name ASC")
    fun getAllTypeLines(): Flow<List<TypeLineEntity>> // INVARIATA

    // --- NUOVA FUNZIONE DI RICERCA FLESSIBILE ---
    @RawQuery(observedEntities = [CardEntity::class]) // Anche se selezioniamo pochi campi, osservare CardEntity per reattività
    fun searchSmallCards(query: SupportSQLiteQuery): Flow<List<SmallPlayingCard>>
}
