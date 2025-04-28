package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Player
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    fun getPlayer() = localDataSource.getPlayer()
    fun getAges() = remoteDataSource.getAvailableAges()
    fun setPlayer(age: Int, gender: String) = localDataSource.setPlayer(Player(gender, age))
}