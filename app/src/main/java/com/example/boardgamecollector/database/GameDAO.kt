package com.example.boardgamecollector.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.boardgamecollector.dataModels.gameHeader

@Dao
interface GameDAO {
    @Insert
    suspend fun insertAll(vararg users: Game)

    @Delete
    suspend fun delete(user: Game)

    @Query("SELECT id,title as title, Description as description,Ranking as ranking,Release_year as  year, Thumb_Image_link as URL FROM Games" +
            " WHERE Game_type LIKE :game")
    suspend fun getAllHeaders(game : String = "boardgame"): List<gameHeader>

    @Query("DELETE  FROM games")
    suspend fun nukeOption()
}