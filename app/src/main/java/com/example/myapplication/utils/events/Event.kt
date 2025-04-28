package com.example.myapplication.utils.events

import java.util.concurrent.atomic.AtomicBoolean

open class Event {
    protected var isHandled = AtomicBoolean()

    fun shouldHandle(): Boolean {
        return isHandled.compareAndSet(false, true)
    }

}