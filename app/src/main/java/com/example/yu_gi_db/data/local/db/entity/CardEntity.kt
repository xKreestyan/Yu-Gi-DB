package com.example.yu_gi_db.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.yu_gi_db.data.local.db.typeconverter.CardConverters // Assicurati che il percorso sia corretto
import com.example.yu_gi_db.model.CardPrice // Assicurati che il percorso sia corretto

@Entity(tableName = "cards")
@TypeConverters(CardConverters::class) // Applica il TypeConverter a questa entità
data class CardEntity(
    @PrimaryKey val id: Int,
    val name: String,
    // val typeline: List<String>, // CAMPO RIMOSSO
    val type: String,
    val humanReadableCardType: String,
    val frameType: String,
    val desc: String,
    val race: String,
    val atk: Int?,
    val def: Int?,
    val level: Int?,
    val attribute: String?,
    val imageUrlSmall: String,
    // Nota: card_sets non è un campo diretto, la relazione è gestita tramite CardSetAppearanceEntity
    val cardPrices: List<CardPrice> // Usa il TypeConverter per questo
)
