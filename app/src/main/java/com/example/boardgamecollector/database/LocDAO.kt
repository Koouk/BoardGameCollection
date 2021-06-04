package com.example.boardgamecollector.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocDAO {
    @Insert
    suspend fun insertAll(vararg users: Location)

    @Delete
    suspend fun delete(user: Location)

    @Query("SELECT * from Locations")
    suspend fun getAllLocalization() : List<Location>

}