package com.example.yu_gi_db.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "type_lines",
    indices = [Index(value = ["name"], unique = true)] // Assicura che i nomi delle typeline siano unici
)
data class TypeLineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)