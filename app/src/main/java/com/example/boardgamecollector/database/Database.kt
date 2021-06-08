package com.example.boardgamecollector.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Game::class, Location::class, RankHistory::class,Artists::class,Designers::class, ArtistsGamesRef::class, DesignersGamesRef::class], version = 8)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): GameDAO
    abstract fun LocDAO(): LocDAO
    abstract fun RankDAO(): RankDAO
    abstract fun ArtistsDAO(): ArtistsDAO
    abstract fun DesignersDAO(): DesignersDAO
    abstract fun ArtistsGameDAO(): ArtistsGameDAO
    abstract fun DesignersGameDAO(): DesignersGameDAO
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "BGCdb"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}