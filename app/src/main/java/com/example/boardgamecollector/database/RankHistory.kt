package com.example.boardgamecollector.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "rankHistory")
data class RankHistory(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "Game_id")
    var gameID: Int,
    @ColumnInfo(name = "Rank")
    var rank: Int = 0,
    @ColumnInfo(name = "Date")
    var untilDate: LocalDateTime

)
