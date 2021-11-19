package com.example.boardgamecollector.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Designers constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String = "",
    var surname: String = "",
    var bggID: Long = -1
)

