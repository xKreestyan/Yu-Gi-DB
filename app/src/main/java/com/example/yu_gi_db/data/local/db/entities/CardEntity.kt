package com.example.yu_gi_db.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.yu_gi_db.data.local.db.typeconverter.CardConverters
import com.example.yu_gi_db.model.CardPrice

@Entity(tableName = "cards")
@TypeConverters(CardConverters::class)
data class CardEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val humanReadableCardType: String, // Questo dovrebbe venire dall'API ora
    val frameType: String,
    val desc: String,
    val race: String,
    val atk: Int?,
    val def: Int?,
    val level: Int?,
    val attribute: String?,
    val localImageSmallPath: String?, // MODIFICATO: per memorizzare il percorso locale dell'immagine piccola
    // val imageUrl: String?, // OPZIONALE: se vuoi salvare anche l'URL dell'immagine grande (non localmente)
    val cardPrices: List<CardPrice>
)

