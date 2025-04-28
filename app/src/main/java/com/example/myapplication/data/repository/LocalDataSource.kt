package com.example.myapplication.data.repository

import android.content.SharedPreferences
import com.example.myapplication.data.model.Player
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    fun getPlayer(): Player {
        return Player(sharedPrefs.getString(KEY_GENDER, "")!!, sharedPrefs.getInt(KEY_AGE, -1))
    }

    fun setPlayer(player: Player) {
        sharedPrefs.edit().apply {
            putInt(KEY_AGE, player.age)
            putString(KEY_GENDER, player.gender)
        }.apply()
    }

    companion object {
        const val KEY_AGE = "age"
        const val KEY_GENDER = "gender"
    }

}