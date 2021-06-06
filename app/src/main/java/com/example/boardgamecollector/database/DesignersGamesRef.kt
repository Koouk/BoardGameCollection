package com.example.boardgamecollector.database

import androidx.room.Entity

@Entity(primaryKeys = ["GameId","DesignerId"])
data class DesignersGamesRef(
    val GameId: Int,
    val DesignerId: Long
)