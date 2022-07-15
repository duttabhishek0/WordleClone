package com.abhishek.wordleclone.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abhishek.wordleclone.domain.dao.GameStatsdao
import com.abhishek.wordleclone.domain.entity.GameStats

@Database(entities = [GameStats::class], version = 1)
abstract class WordleDatabase : RoomDatabase() {
    abstract fun gameStatsDao(): GameStatsdao

    companion object {
        @Volatile
        private var INSTANCE: WordleDatabase? = null

        fun getInstance(context: Context): WordleDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildInstance(context).also { INSTANCE = it }
            }

        private fun buildInstance(context: Context) =
            Room.databaseBuilder(
                context.applicationContext, WordleDatabase::class.java, "worldle.db"
            ).build()
    }


}