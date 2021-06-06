package com.example.boardgamecollector.database

import androidx.room.*


@Dao
interface DesignersGameDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: DesignersGamesRef) : Long
    @Insert
    suspend fun insertAll(vararg users: DesignersGamesRef)
    @Delete
    suspend fun delete(user: DesignersGamesRef)

    @Query("SELECT id,name,surname,bggID FROM DesignersGamesRef JOIN Designers ON id = DesignerId WHERE GameId = :id")
    fun getDesignersOfGame(id : Int): List<Designers>

}