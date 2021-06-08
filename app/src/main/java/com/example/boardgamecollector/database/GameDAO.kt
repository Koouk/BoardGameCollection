package com.example.boardgamecollector.database

import androidx.room.*
import com.example.boardgamecollector.dataModels.LocHeader
import com.example.boardgamecollector.dataModels.RanksHeader
import com.example.boardgamecollector.dataModels.gameHeader

@Dao
interface GameDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg users: Game)  : List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(users: Game)  : Long
    @Delete
    suspend fun delete(user: Game)

    @Update
    fun updateGame(vararg users: Game)

    @Query("SELECT id,title as title, Description as description,Ranking as ranking,Release_year as  year, Thumb_Image_link as URL FROM Games" +
            " WHERE Game_type LIKE :game")
    suspend fun getAllHeaders(game : String = "boardgame"): List<gameHeader>

    @Query("SELECT * FROM Games" +
            " WHERE id LIKE :id")
    suspend fun getByID(id : Long): Game

    @Query("SELECT id,title as title, Description as description,Ranking as ranking,Release_year as  year, Thumb_Image_link as URL FROM Games"
    + " WHERE Parent_bgg = :parentID ")
    suspend fun getAllExtensions(parentID : Long) : List<gameHeader>

    @Query("DELETE  FROM games")
    suspend fun nukeOption()


    @Query("Select id,title as title ,Release_year as  year FROM games WHERE localizationID = :locID")
    suspend fun getGamesByLocation(locID : Long) : List<LocHeader>

    @Query("Select id,ranking as ranking, BGG_id as bggId FROM  games WHERE Game_type LIKE 'boardgame' AND BGG_id > 0")
    suspend fun getGamesRanking() : List<RanksHeader>

    @Query("UPDATE Games SET ranking = :rank WHERE id = :id")
    suspend fun updateRanking(id : Int, rank : Int)
}