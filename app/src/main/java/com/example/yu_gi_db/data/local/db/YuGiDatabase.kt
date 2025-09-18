package com.example.yu_gi_db.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yu_gi_db.data.local.db.dao.YuGiDAO
import com.example.yu_gi_db.data.local.db.entities.CardEntity
import com.example.yu_gi_db.data.local.db.entities.CardSetAppearanceEntity
import com.example.yu_gi_db.data.local.db.entities.SetEntity
import com.example.yu_gi_db.data.local.db.entities.TypeLineEntity
import com.example.yu_gi_db.data.local.db.entities.CardTypeLineCrossRef
import com.example.yu_gi_db.data.local.db.entities.CardLocalizationEntity // NUOVO IMPORT per la localizzazione
import com.example.yu_gi_db.data.local.db.typeconverter.CardConverters

@Database(
    entities = [
        CardEntity::class,
        SetEntity::class,
        CardSetAppearanceEntity::class,
        TypeLineEntity::class,
        CardTypeLineCrossRef::class,
        CardLocalizationEntity::class // NUOVA ENTITÀ PER LA LOCALIZZAZIONE
    ],
    version = 4, // VERSIONE INCREMENTATA
    exportSchema = false // Potresti volerlo impostare a true per i test di migrazione futuri
)
@TypeConverters(CardConverters::class)
abstract class YuGiDatabase : RoomDatabase() {

    abstract fun yuGiDao(): YuGiDAO

}
