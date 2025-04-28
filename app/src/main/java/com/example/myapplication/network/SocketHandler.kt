package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.data.model.Player
import com.example.myapplication.data.model.ResultEvent
import com.example.myapplication.utils.coroutine_scope.IOCoroutineScope
import com.google.gson.Gson
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Оставил как пример изначальной реализации через WebSocket
@Singleton
class SocketHandler @Inject constructor(
    val gson: Gson
) : IOCoroutineScope {
    private var reconnectingJob: Job? = null
    private lateinit var socket: WebSocket
    private lateinit var eventListener: EventListener


    private val socketListener: WebSocketAdapter = object : WebSocketAdapter() {
        override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
            super.onConnectError(websocket, exception)
        }

        override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
            super.onError(websocket, cause)
        }

        override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
            super.onUnexpectedError(websocket, cause)
        }

        override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
            eventListener.onSocketConnect()
        }

        override fun onMessageError(
            websocket: WebSocket?,
            cause: WebSocketException?,
            frames: MutableList<WebSocketFrame>?
        ) {
            super.onMessageError(websocket, cause, frames)
        }

        override fun onDisconnected(
            websocket: WebSocket?,
            serverCloseFrame: WebSocketFrame?,
            clientCloseFrame: WebSocketFrame?,
            closedByServer: Boolean
        ) {
            if (closedByServer) {
                eventListener.onSocketDisconnect()
                reconnectingJob = reconnect(120)
            }
        }

        override fun onTextMessage(websocket: WebSocket?, text: String?) {
            val event = gson.fromJson(text, ResultEvent::class.java)

            eventListener.onResultEvent(event)
        }
    }

    fun connect(eventListener: EventListener) {
        init(eventListener)
        try {
            socket.connect()
        } catch (e: WebSocketException) {
            Log.d("ERROR", e.message?: "null")
            eventListener.onSocketConnectionFailed()
        }
    }

    fun isConnected(): Boolean {
        return ::socket.isInitialized && socket.isOpen
    }


    private fun sendEvent(json: String) {
        socket.sendText(json)
    }

    fun sendPlayerEvent(gender: String, age: Int) {
        val eventJson = gson.toJson(
            Player(gender, age)
        )
        sendEvent(eventJson)
    }

    fun dispose() {
        reconnectingJob?.cancel()
        if (::socket.isInitialized) {
            socket.removeListener(socketListener)
            socket.disconnect()
        }
    }

    private fun init(listener: EventListener) {
        socket = WebSocketFactory()
//            .trustAllCerts(appContext)
            .createSocket(
                String.format(SOCKET_URL_FORMAT, SOCKET_SERVER_URL , SOCKET_SERVER_PORT),
                SOCKET_CONNECTION_TIMEOUT
            )

        socket.addListener(socketListener)
        eventListener = listener

    }

    private fun reconnect(attempts: Int) = launch(Dispatchers.IO) {
        var isConnected = false
        var attemptsCount = attempts

        while (!isConnected && attemptsCount > 0) {
            try {
                reconnect()
                attemptsCount -= 1
                isConnected = true
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: WebSocketException) {
                e.printStackTrace()
            }
            delay(1000)
        }

        if (!isConnected) {
            eventListener.onSocketConnectionFailed()
        }
    }

    private fun reconnect() {
        socket = socket.recreate().connect()
    }

    interface EventListener {
        fun onSocketConnect()
        fun onSocketDisconnect()
        fun onSocketConnectionFailed()
        fun onResultEvent(event: ResultEvent)
    }

    companion object {
        private const val SOCKET_CONNECTION_TIMEOUT = 3000
        private const val SOCKET_URL_FORMAT = "http://%s:%s"
        private const val SOCKET_SERVER_URL = "challenge.ciliz.com"
        private const val SOCKET_SERVER_PORT = "2222"
    }

}