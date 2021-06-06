package com.example.boardgamecollector.database

import androidx.room.*

@Dao
interface ArtistsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: Artists)

    @Insert
    suspend fun insert(user: Artists) : Long

    @Delete
    suspend fun delete(user: Artists)


    @Query("SELECT COUNT(*) from Artists WHERE bggID LIKE :id")
    suspend fun checkIfExists(id : Long) : Int

}