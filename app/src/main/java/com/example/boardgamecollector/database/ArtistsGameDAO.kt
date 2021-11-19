package com.example.boardgamecollector.database

import androidx.room.*


@Dao
interface ArtistsGameDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: ArtistsGamesRef): Long

    @Insert
    suspend fun insertAll(vararg users: ArtistsGamesRef)


    @Delete
    suspend fun delete(user: ArtistsGamesRef)

    @Query("DELETE  FROM ArtistsGamesRef WHERE GameId = :id")
    suspend fun nukeOptionID(id: Int)

    @Query("SELECT id,name,surname,bggID FROM ArtistsGamesRef JOIN Artists ON id = ArtistId WHERE GameId = :id")
    fun getArtistsOfGame(id: Int): List<Artists>

}