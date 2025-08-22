package com.example.yu_gi_db.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "card_type_line_cross_ref",
    primaryKeys = ["cardId", "typeLineId"], // Chiave primaria composita
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE // Se una carta viene cancellata, cancella anche le sue associazioni typeline
        ),
        ForeignKey(
            entity = TypeLineEntity::class,
            parentColumns = ["id"],
            childColumns = ["typeLineId"],
            onDelete = ForeignKey.CASCADE // Se una typeline viene cancellata, cancella le associazioni
        )
    ]
)
data class CardTypeLineCrossRef(
    val cardId: Int,
    val typeLineId: Long
)
