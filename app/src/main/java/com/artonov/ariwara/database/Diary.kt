package com.artonov.ariwara.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Diary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mood: String,
    val note: String,
    val date: String,
    val count: Int = 0
)