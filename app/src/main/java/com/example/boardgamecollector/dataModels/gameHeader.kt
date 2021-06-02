package com.example.boardgamecollector.dataModels

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Ignore


data class gameHeader(
    var id: Int,
    val title: String?,
    val description: String? = null,
    val ranking: Int?,
    val year: String?,
    val URL : String? = null,
)
{
    @Ignore
    var image: Bitmap? = null
}