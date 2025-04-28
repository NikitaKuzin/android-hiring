package com.example.myapplication.utils.events

class DataEvent<T>(private val data: T): Event() {

    fun handleAndGet(): T? {
        return if (shouldHandle()) data else null
    }
}