package com.example.boardgamecollector.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    @TypeConverter
    fun fromDateToString(value : LocalDate?) : String?{
        if (value != null) {
            return value.format(formatter)
        }
        return null
    }

    @TypeConverter
    fun fromStringToDate(value : String?) : LocalDate? {
        return LocalDate.parse(value,formatter)
    }

}