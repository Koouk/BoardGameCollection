package com.example.boardgamecollector.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Locations")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "Description")
    val description : String?,
    @ColumnInfo(name = "Name")
    val name : String?

    )
{

}