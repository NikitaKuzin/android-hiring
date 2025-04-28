package com.example.myapplication.data.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule {
    @Singleton
    @Provides
    fun providePreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun getGson(): Gson {
        return Gson()
    }

    companion object {
        private const val PREFERENCES = "preferences"
    }
}