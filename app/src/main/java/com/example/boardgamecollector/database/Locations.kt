package com.example.boardgamecollector.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Locations")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "Description")
    var description : String?,
    @ColumnInfo(name = "Name")
    var name : String?

    )
