package com.example.myapplication.utils.coroutine_scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

interface IOCoroutineScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()
}