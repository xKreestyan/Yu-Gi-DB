package com.example.yu_gi_db.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sets",
    indices = [Index(value = ["name"], unique = true)]
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String // Esempio: "Maximum Crisis"
)
