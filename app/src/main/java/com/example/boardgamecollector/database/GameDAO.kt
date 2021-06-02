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

    @Query("SELECT id,title as title, Description as description,Ranking as ranking,Release_year as  year, Image_link as URL FROM Games")
    suspend fun getAllHeaders(): List<gameHeader>
}