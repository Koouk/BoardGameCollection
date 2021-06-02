package com.example.boardgamecollector.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity (tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "Title")
    val title : String?,
    @ColumnInfo(name = "Original_title")
    val originalTitle : String?,
    @ColumnInfo(name = "Release_year")
    val releaseYear : String?,
    @ColumnInfo(name = "Description")
    val description : String?,
    @ColumnInfo(name = "Order_date")
    val orderDate : LocalDate?,
    @ColumnInfo(name = "Add_date")
    val addDate : LocalDate?,
    @ColumnInfo(name = "Cost")
    val cost : String?,
    @ColumnInfo(name = "SCD")
    val scd : String?,
    @ColumnInfo(name = "EAN")
    val ean : String?,
    @ColumnInfo(name = "BGG_id")
    val bggId : Long?,
    @ColumnInfo(name = "Production_code")
    val productionCode : String?,
    @ColumnInfo(name = "Ranking")
    val ranking : Int?,
    @ColumnInfo(name = "Game_type")
    val gameType : String?,
    @ColumnInfo(name = "Comment")
    val comment : String?,
    @ColumnInfo(name = "Image_link")
    val url : String?,

)
{
    @Ignore val bitmap : Bitmap? = null
}




