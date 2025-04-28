package com.example.myapplication.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor() {
    fun getAvailableAges(): List<Int> {
        //Заготовка на случай если данные по возрасту приходят с бекенда
        return (START_AGE..END_AGE).toList()
    }

    companion object {
        const val START_AGE = 16
        const val END_AGE = 30
    }
}