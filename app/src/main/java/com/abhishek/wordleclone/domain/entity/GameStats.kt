package com.abhishek.wordleclone.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhishek.wordleclone.R

@Entity
data class GameStats(
    @PrimaryKey(autoGenerate = false) var uid: Int = 1,
    @ColumnInfo(name="played") var gamePlayed: Int=0,
    @ColumnInfo(name="streak") var streak: Int=0,
    @ColumnInfo(name="max_streak") var maxStreak: Int=0,
    @ColumnInfo(name="one") var alpha:Int = 0,
    @ColumnInfo(name="two") var beta:Int = 0,
    @ColumnInfo(name="three") var gamma:Int = 0,
    @ColumnInfo(name="four") var delta:Int = 0,
    @ColumnInfo(name="five") var epsilon:Int = 0,
    @ColumnInfo(name="six") var zeta:Int = 0
) {
    fun levelCleared(level: Int) {
        gamePlayed++
        streak++

        if(streak > maxStreak) maxStreak = streak

        when(level) {
            0 -> alpha++
            1 -> beta++
            2 -> gamma++
            3 -> delta++
            4 -> epsilon++
            5 -> zeta++
        }
    }

    fun levelFailed(){
        gamePlayed++
        streak = 0
    }
}
