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
    // val name: String, // Rimosso
    val type: String, // Mantenuto - sembra essere un dato tecnico
    // val humanReadableCardType: String, // Rimosso
    val frameType: String,
    // val desc: String, // Rimosso
    val race: String,
    val atk: Int?,
    val def: Int?,
    val level: Int?,
    val attribute: String?,
    val localImagePath: String?,
    val cardPrices: List<CardPrice>,
    val isFavorite: Boolean = false
)
