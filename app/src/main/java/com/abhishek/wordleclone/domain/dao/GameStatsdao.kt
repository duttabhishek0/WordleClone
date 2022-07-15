package com.abhishek.wordleclone.domain.dao

import androidx.room.*
import com.abhishek.wordleclone.domain.entity.GameStats

@Dao
interface GameStatsdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStat(stat: GameStats)

    @Query("SELECT * FROM GameStats WHERE uid IS :userId")
    fun getStat(userId: Int): GameStats

    @Update
    fun updateStat(newStat: GameStats)
}