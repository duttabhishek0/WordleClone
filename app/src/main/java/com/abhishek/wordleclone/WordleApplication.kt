package com.abhishek.wordleclone

import android.app.Application
import com.abhishek.wordleclone.domain.WordleDatabase

class WordleApplication : Application() {
    val wordleDatabase by lazy {
        WordleDatabase.getInstance(this)
    }
}