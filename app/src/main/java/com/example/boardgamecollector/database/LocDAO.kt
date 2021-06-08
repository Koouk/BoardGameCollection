package com.example.boardgamecollector.database

import androidx.room.*

@Dao
interface LocDAO {
    @Insert
    suspend fun insertAll(vararg users: Location)

    @Delete
    suspend fun delete(user: Location)

    @Query("SELECT * from Locations")
    suspend fun getAllLocalization() : List<Location>

    @Query("SELECT * from Locations WHERE id LIKE :id")
    suspend fun getLocById(id : Long) : Location?

    @Update
    fun update(vararg users: Location)

}