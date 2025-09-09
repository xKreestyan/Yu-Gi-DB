package com.example.yu_gi_db.model

data class AdvancedSearchCriteria(
    var name: String? = null,           // Corrisponde a CardEntity.name
    var type: String? = null,           // Corrisponde a CardEntity.type (es. "Effect Monster", "Spell Card")
    var attribute: String? = null,      // Corrisponde a CardEntity.attribute (es. "LIGHT", "DARK")
    var level: Int? = null,             // Corrisponde a CardEntity.level (o rank)
    var atkMin: Int? = null,            // Per la ricerca ATK in un range (>= atkMin)
    var atkMax: Int? = null,            // Per la ricerca ATK in un range (<= atkMax)
    var defMin: Int? = null,            // Per la ricerca DEF in un range (>= defMin)
    var defMax: Int? = null,            // Per la ricerca DEF in un range (<= defMax)
    var idQuery: String? = null,        // NUOVO CAMPO per la ricerca tramite ID (come stringa)
    var isFavorite: Boolean? = null     // NUOVO CAMPO per filtrare per preferiti
)
