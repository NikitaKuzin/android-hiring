package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.di.AppComponent
import com.example.myapplication.data.di.DaggerAppComponent

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
    }
}