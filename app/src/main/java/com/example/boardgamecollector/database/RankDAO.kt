package com.example.boardgamecollector.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RankDAO {
    @Insert
    suspend fun insertAll(vararg users: RankHistory)

    @Delete
    suspend fun delete(user: RankHistory)

    @Query("SELECT * from rankHistory WHERE Game_id LIKE :id")
    suspend fun getRankById(id: Int) : List<RankHistory>

}