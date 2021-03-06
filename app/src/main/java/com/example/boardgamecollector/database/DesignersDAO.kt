package com.example.boardgamecollector.database

import androidx.room.*

@Dao
interface DesignersDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: Designers)

    @Insert
    suspend fun insert(user: Designers): Long

    @Delete
    suspend fun delete(user: Designers)

    @Query("SELECT * from Designers")
    suspend fun getAll(): List<Designers>

    @Query("SELECT COUNT(*) from Designers WHERE bggID LIKE :id")
    suspend fun checkIfExists(id: Long): Int

    @Query("SELECT id from Designers WHERE bggID LIKE :id")
    suspend fun getID(id: Long): Long
}