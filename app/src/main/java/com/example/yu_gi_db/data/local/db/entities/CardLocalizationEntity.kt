package com.example.yu_gi_db.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "card_localizations",
    primaryKeys = ["cardId", "languageCode"],
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE // Se una carta viene eliminata, elimina anche le sue localizzazioni
        )
    ]
)
data class CardLocalizationEntity(
    val cardId: Int,
    val languageCode: String, // es. "it", "en"
    val name: String,
    val desc: String,
    val humanReadableCardType: String
)
