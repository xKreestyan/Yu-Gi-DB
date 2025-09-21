package com.example.yu_gi_db.data.local.db.typeconverter

import androidx.room.TypeConverter
import com.example.yu_gi_db.model.CardPrice
// import com.example.yu_gi_db.model.CardSet // Rimosso
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CardConverters {
    private val gson = Gson()

    // Converter per List<CardPrice>
    @TypeConverter
    fun fromCardPriceList(value: List<CardPrice>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toCardPriceList(value: String?): List<CardPrice>? {
        return value?.let { gson.fromJson(it, object : TypeToken<List<CardPrice>>() {}.type) }
    }
}
