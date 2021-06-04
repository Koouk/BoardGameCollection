package com.example.boardgamecollector.database

import android.graphics.Bitmap
import androidx.room.*
import java.time.LocalDate

@Entity (tableName = "games",foreignKeys = arrayOf(
    ForeignKey(entity = Location::class,
        parentColumns = arrayOf("id"),childColumns = arrayOf("localizationID"),onDelete = ForeignKey.RESTRICT )
)
)
data class Game constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "Title")
    var title : String? = null,
    @ColumnInfo(name = "Original_title")
    var originalTitle : String? = null,
    @ColumnInfo(name = "Release_year")
    var releaseYear : String? = null,
    @ColumnInfo(name = "Description")
    var description : String? = null,
    @ColumnInfo(name = "Order_date")
    var orderDate : LocalDate? = null,
    @ColumnInfo(name = "Add_date")
    var addDate : LocalDate? = null,
    @ColumnInfo(name = "Cost")
    var cost : String? = null,
    @ColumnInfo(name = "SCD")
    var scd : String? = null,
    @ColumnInfo(name = "EAN")
    var ean : String? = null,
    @ColumnInfo(name = "BGG_id")
    var bggId : Long = 0,
    @ColumnInfo(name = "Production_code")
    var productionCode : String? = null,
    @ColumnInfo(name = "Ranking")
    var ranking : Int = 0,
    @ColumnInfo(name = "Game_type")
    var gameType : String? = null,
    @ColumnInfo(name = "Parent_bgg")
    var parentBGG : Long? = null,
    @ColumnInfo(name = "Comment")
    var comment : String? = null,
    @ColumnInfo(name = "Thumb_Image_link")
    var ThumbURL : String? = null,
    @ColumnInfo(name = "Big_Image_link")
    var ImgURL : String? = null,
    @ColumnInfo(name = "localizationID", index = true)
    var localizationID: Int? = null

    )
{
    @Ignore var bitmap : Bitmap? = null

}




