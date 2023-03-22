package com.onthewake.onthewakelive.feature_queue.data.repository

import android.content.SharedPreferences
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_FIRST_NAME
import com.onthewake.onthewakelive.core.utils.Constants.WS_BASE_URL
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.SimpleResource
import com.onthewake.onthewakelive.core.utils.handleNetworkError
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueResponse
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.*

class QueueSocketServiceImpl(
    private val client: HttpClient,
    private val prefs: SharedPreferences
) : QueueSocketService {
    private var socket: WebSocketSession? = null

    override suspend fun initSession(
        firstName: String
    ): Resource<Unit> = try {
        socket = client.webSocketSession {
            url(urlString = "$WS_BASE_URL?firstName=$firstName")
        }
        if (socket?.isActive == true) Resource.Success(Unit)
        else Resource.Error(UIText.StringResource(R.string.couldnt_establish_a_connection))
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    override fun observeQueue(): Flow<QueueResponse> = socket!!
        .incoming
        .consumeAsFlow()
        .filterIsInstance<Frame.Text>()
        .mapNotNull { Json.decodeFromString<QueueResponse>(it.readText()) }

    override suspend fun addToQueue(isLeftQueue: Boolean, firstName: String?): SimpleResource =
        try {
            val name = firstName ?: prefs.getString(PREFS_FIRST_NAME, null)
            val timestamp = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()

            socket?.send(Frame.Text("$isLeftQueue/$name/$timestamp"))
            Resource.Success(Unit)
        } catch (exception: Exception) {
            Resource.Error(handleNetworkError(exception))
        }

    override suspend fun closeSession() {
        socket?.close()
        socket = null
    }
}
