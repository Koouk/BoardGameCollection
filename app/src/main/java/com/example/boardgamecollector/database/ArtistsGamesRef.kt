package com.example.boardgamecollector.database

import androidx.room.Entity

@Entity(primaryKeys = ["GameId", "ArtistId"])
data class ArtistsGamesRef(
    val GameId: Int,
    val ArtistId: Long
)
