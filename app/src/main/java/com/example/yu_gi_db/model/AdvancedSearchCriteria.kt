package com.example.yu_gi_db.model

data class AdvancedSearchCriteria(
    val name: String? = null,           // Corrisponde a CardEntity.name
    val type: String? = null,           // Corrisponde a CardEntity.type (es. "Effect Monster", "Spell Card")
    val attribute: String? = null,      // Corrisponde a CardEntity.attribute (es. "LIGHT", "DARK")
    val level: Int? = null,             // Corrisponde a CardEntity.level (o rank)
    val atkMin: Int? = null,            // Per la ricerca ATK in un range (>= atkMin)
    val atkMax: Int? = null,            // Per la ricerca ATK in un range (<= atkMax)
    val defMin: Int? = null,            // Per la ricerca DEF in un range (>= defMin)
    val defMax: Int? = null             // Per la ricerca DEF in un range (<= defMax)
)
