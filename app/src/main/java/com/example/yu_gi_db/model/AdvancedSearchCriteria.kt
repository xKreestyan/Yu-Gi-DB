package com.example.yu_gi_db.model

data class AdvancedSearchCriteria(
    val idQuery: String? = null,           // Per cercare ID parziali come stringa
    val name: String? = null,
    val type: String? = null,           // Corrisponde a CardEntity.type (es. "Effect Monster", "Spell Card")
    val raceQuery: String? = null, // Per cercare la razza (es. "Equip")
    val attribute: String? = null,      // Corrisponde a CardEntity.attribute (es. "LIGHT", "DARK")
    val level: Int? = null,             // Corrisponde a CardEntity.level (o rank)
    val atkMin: Int? = null,            // Per la ricerca ATK in un range (>= atkMin)
    val atkMax: Int? = null,            // Per la ricerca ATK in un range (<= atkMax)
    val defMin: Int? = null,            // Per la ricerca DEF in un range (>= defMin)
    val defMax: Int? = null,            // Per la ricerca DEF in un range (<= defMax)
    val isFavorite: Boolean? = null,     // Per filtrare per preferiti
    val setNameQuery: String? = null,    // NUOVO CAMPO per nome del set
    val setCodeQuery: String? = null     // NUOVO CAMPO per codice del set
)
