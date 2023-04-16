package com.onthewake.onthewakelive.feature_queue.domain.repository

import com.onthewake.onthewakelive.core.utils.SimpleResource
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueSocketResponse
import kotlinx.coroutines.flow.Flow

interface QueueSocketService {
    suspend fun initSession(): SimpleResource
    fun isConnectionEstablished(): Boolean
    fun observeQueue(): Flow<QueueSocketResponse>
    suspend fun joinTheQueue(line: Line, firstName: String? = null): SimpleResource
    suspend fun leaveTheQueue(queueItemId: String): SimpleResource
    suspend fun closeSession()
}