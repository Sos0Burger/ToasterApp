package com.messenger.toaster.repository

import android.util.Log
import com.google.gson.Gson
import com.messenger.toaster.api.RetrofitClient
import com.messenger.toaster.data.ActionEnum
import com.messenger.toaster.data.User
import com.messenger.toaster.dto.ResponseWebsocketMessageDTO
import com.messenger.toaster.viewmodel.MessagesViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.ws
import io.ktor.client.request.header
import io.ktor.http.HttpMethod
import io.ktor.websocket.readBytes
import io.ktor.websocket.send
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class WebSocketRepository {
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }
    private val gson = Gson()

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages
    suspend fun connectWebSocket(viewModel: MessagesViewModel) {
        client.ws(
            method = HttpMethod.Get,
            host = RetrofitClient.getInstance().baseUrl().host,
            port = 8080,
            path = "/ws",
            request = {
                header("Authorization", User.getCredentials())
            }
        ) {
            try {
                while (true) {
                    val data = incoming.receive().readBytes()
                    val message =
                        gson.fromJson(String(data), ResponseWebsocketMessageDTO::class.java)
                    when (message.action) {
                        ActionEnum.CREATE -> {
                            viewModel.addMessage(message.toMessageDTO())
                        }

                        ActionEnum.UPDATE -> {
                            viewModel.updateMessage(message.toMessageDTO())
                        }

                        ActionEnum.DELETE -> {
                            viewModel.removeMessage(message.toMessageDTO())
                        }
                    }

                }
            } catch (e: Exception) {
                Log.d("WebSocket", "WebSocket error: ${e.message}")
            }
        }
        client.close()
    }

    suspend fun sendMessage(messages: List<ResponseWebsocketMessageDTO>, onEnd: () -> Unit) {
        val sendClient = HttpClient(CIO) {
            install(WebSockets) {
                pingInterval = 20_000
            }
        }
        sendClient.ws(
            method = HttpMethod.Get,
            host = RetrofitClient.getInstance().baseUrl().host,
            port = 8080,
            path = "/ws",
            request = {
                header("Authorization", User.getCredentials())
            }
        ) {
            messages.forEach(action = {
                val bytes = gson.toJson(it).toByteArray()
                send(bytes)
            })

        }
        onEnd()
        sendClient.close()
    }
}