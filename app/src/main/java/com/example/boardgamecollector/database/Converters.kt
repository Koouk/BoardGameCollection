package com.example.boardgamecollector.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    val formatter2 = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")

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


    @TypeConverter
    fun fromDateTimeToString(value : LocalDateTime?) : String?{
        if (value != null) {
            return value.format(formatter2)
        }
        return null
    }

    @TypeConverter
    fun fromStringToDateTime(value : String?) : LocalDateTime? {
        return LocalDateTime.parse(value,formatter2)
    }

}