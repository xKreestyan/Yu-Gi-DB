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
    val humanReadableCardType: String,
    val frameType: String,
    val desc: String,
    val race: String,
    val atk: Int?,
    val def: Int?,
    val level: Int?,
    val attribute: String?,
    val localImagePath: String?, // RINOMINATO da localImageSmallPath
    val cardPrices: List<CardPrice>
)
