package com.example.yu_gi_db.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "card_set_appearances",
    primaryKeys = ["cardId", "setSpecificCode"], // Una carta in un set con un codice specifico è unica
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE // Se una carta viene eliminata, elimina le sue apparizioni nei set
        ),
        ForeignKey(
            entity = SetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE // Se un set viene eliminato, elimina le apparizioni delle carte in esso
        )
    ],
    indices = [
        Index(value = ["cardId"]),
        Index(value = ["setId"]),
        Index(value = ["setSpecificCode"], unique = true) // Assicuriamoci che ogni codice di set specifico sia unico
    ]
)
data class CardSetAppearanceEntity(
    val cardId: Int, // Foreign key to CardEntity
    val setId: Long, // Foreign key to SetEntity

    @ColumnInfo(name = "setSpecificCode") // Per evitare ambiguità con "set_code" dell'API
    val setSpecificCode: String,      // Es. "MACR-EN081"
    val rarity: String,               // Es. "Secret Rare"
    val rarityCode: String,           // Es. "(ScR)"
    val price: String                 // Es. "0" o "1.58" (memorizzato come Stringa dall'API)
)
