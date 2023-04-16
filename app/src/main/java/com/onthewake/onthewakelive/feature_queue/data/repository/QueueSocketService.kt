//package com.onthewake.onthewakelive.feature_queue.data.repository
//
//import com.onthewake.onthewakelive.core.utils.Constants
//import com.onthewake.onthewakelive.feature_queue.data.remote.model.QueueSocketMessage
//import com.onthewake.onthewakelive.feature_queue.domain.module.Action
//import com.onthewake.onthewakelive.feature_queue.domain.module.Line
//import com.onthewake.onthewakelive.feature_queue.domain.module.QueueSocketResponse
//import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.QueueViewModel
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.WebSocket
//import okhttp3.WebSocketListener
//
//class QueueSocketService(
//    private val viewModel: QueueViewModel,
//    okHttpClient: OkHttpClient
//) : WebSocketListener() {
//
//    private var webSocket: WebSocket? = null
//
//    init {
//        if (webSocket == null) okHttpClient.newWebSocket(
//            request = Request.Builder().url(Constants.WS_BASE_URL).build(),
//            listener = this
//        )
//    }
//
//    override fun onOpen(webSocket: WebSocket, response: Response) {
//        super.onOpen(webSocket, response)
//        this.webSocket = webSocket
//    }
//
//    override fun onMessage(webSocket: WebSocket, text: String) {
//        super.onMessage(webSocket, text)
//        val queueItem = Json.decodeFromString<QueueSocketResponse>(text)
//        viewModel.onMessage(queueItem)
//    }
//
//    fun joinTheQueue(line: Line, firstName: String? = null) {
//        val queueSocketMessage = Json.encodeToString(
//            QueueSocketMessage(
//                action = Action.JOIN_THE_QUEUE,
//                line = line,
//                firstName = firstName
//            )
//        )
//        webSocket?.send(queueSocketMessage)
//    }
//
//    fun leaveTheQueue(queueItemId: String) {
//        val queueSocketMessage = Json.encodeToString(
//            QueueSocketMessage(
//                action = Action.LEAVE_THE_QUEUE,
//                queueItemId = queueItemId
//            )
//        )
//        webSocket?.send(queueSocketMessage)
//    }
//
//    fun closeSession() {
//        webSocket?.cancel()
//    }
//}