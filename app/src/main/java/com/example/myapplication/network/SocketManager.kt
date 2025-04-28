package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.data.model.Player
import com.example.myapplication.data.model.ResultEvent
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.DataInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "SocketManager"

@Singleton
class SocketManager @Inject constructor(
    private val gson: Gson
) {

    private var address: String? = null
    private var port: Int? = null

    private var socket: Socket? = null

    private var errorListener: ErrorListener? = null

    fun setErrorListener(listener: ErrorListener) {
        errorListener = listener
    }

    fun connect(address: String, port: Int) {
        try {
            if (socket == null) {
                socket = Socket(address, port)
            }
            if (!socket!!.isConnected) {
                socket!!.connect(InetSocketAddress(address, port))
            }

            this.address = address
            this.port = port

        } catch (e: Exception) {
            errorListener?.onError(e.message.orEmpty())
        }

        Log.d(TAG, "connected: ${socket?.isConnected}")
    }

    fun send(player: Player): ResultEvent? {
        if (address != null && port != null) {
            connect(address!!, port!!)
        } else {
            return null
        }

        val message = gson.toJson(player) // <- request

        Log.i(TAG, "sending: $message")

        send(message)

        return gson.fromJson(receive(), ResultEvent::class.java)
    }

    private fun send(message: String) {
        Log.i(TAG, "sending: $message")

        val messageBytes = message.toByteArray()
        val lengthBytes = ByteBuffer.allocate(4).putInt(messageBytes.size).array()

        val outputStream = socket?.getOutputStream()
        outputStream?.write(lengthBytes)
        outputStream?.write(messageBytes)
        outputStream?.flush()
    }

    private fun receive(): String {
        val inputStream = DataInputStream(socket?.getInputStream())

        val lengthBytes = ByteArray(4)
        inputStream.readFully(lengthBytes)
        val length = ByteBuffer.wrap(lengthBytes).int

        val buffer = ByteArray(length)
        inputStream.readFully(buffer)
        val message = String(buffer, 0, length)

        Log.d(TAG, "received: $message")

        return message // <- message
    }

    fun close() {
        socket?.close()
        socket = null
        address = null
        port = null
    }

    interface ErrorListener {
        fun onError(message: String)
    }
}
