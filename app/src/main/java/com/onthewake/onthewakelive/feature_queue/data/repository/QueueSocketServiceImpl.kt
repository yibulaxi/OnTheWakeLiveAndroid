package com.onthewake.onthewakelive.feature_queue.data.repository

import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants.WS_BASE_URL
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.SimpleResource
import com.onthewake.onthewakelive.core.utils.handleNetworkError
import com.onthewake.onthewakelive.feature_queue.data.remote.model.QueueSocketMessage
import com.onthewake.onthewakelive.feature_queue.domain.module.Action
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueSocketResponse
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class QueueSocketServiceImpl(private val client: HttpClient) : QueueSocketService {
    private var socket: WebSocketSession? = null

    override suspend fun initSession(): SimpleResource = withContext(Dispatchers.IO) {
        try {
            socket = client.webSocketSession { url(urlString = WS_BASE_URL) }
            if (socket?.isActive == true) Resource.Success(Unit)
            else Resource.Error(UIText.StringResource(R.string.couldnt_establish_a_connection))
        } catch (exception: Exception) {
            Resource.Error(handleNetworkError(exception))
        }
    }

    override fun isConnectionEstablished(): Boolean {
        return socket?.isActive == true && socket != null
    }

    override fun observeQueue(): Flow<QueueSocketResponse> = socket!!
        .incoming
        .consumeAsFlow()
        .filterIsInstance<Frame.Text>()
        .mapNotNull { Json.decodeFromString<QueueSocketResponse>(it.readText()) }

    override suspend fun joinTheQueue(
        line: Line, firstName: String?
    ): SimpleResource = try {
        val queueSocketMessage = Json.encodeToString(
            QueueSocketMessage(
                action = Action.JOIN_THE_QUEUE,
                line = line,
                firstName = firstName
            )
        )
        socket?.send(queueSocketMessage)
        Resource.Success(Unit)
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    override suspend fun leaveTheQueue(
        queueItemId: String
    ): SimpleResource = try {
        val queueSocketMessage = Json.encodeToString(
            QueueSocketMessage(
                action = Action.LEAVE_THE_QUEUE,
                queueItemId = queueItemId
            )
        )
        socket?.send(queueSocketMessage)
        Resource.Success(Unit)
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    override suspend fun closeSession() {
        socket?.close()
        socket = null
    }
}
