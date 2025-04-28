package com.example.myapplication.data.di

import android.content.Context
import com.example.myapplication.ui.MainViewModel

import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferencesModule::class])
interface AppComponent {
    fun inject(mainViewModel: MainViewModel)


    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

}