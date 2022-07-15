package com.abhishek.wordleclone.utils

fun listToWord(list: List<String>, result: String = ""): String {
    if (list.isEmpty()) {
        return result
    }
    return listToWord(list.drop(1), result + list.first())
}

fun wordToList(word: String, wordLength: Int): List<String> {
    return List<String>(wordLength) { word[it].uppercaseChar().toString() }
}