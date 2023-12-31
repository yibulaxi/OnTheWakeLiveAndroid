package com.onthewake.onthewakelive.feature_queue.domain.repository

import com.onthewake.onthewakelive.core.utils.SimpleResource
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueSocketResponse
import kotlinx.coroutines.flow.Flow

interface QueueSocketService {
    suspend fun initSession(): SimpleResource
    fun isConnectionEstablished(): Boolean
    fun observeQueue(): Flow<QueueSocketResponse>
    fun canJoinTheQueue(line: Line, queue: List<QueueItem>): SimpleResource
    suspend fun addUserToTheQueue(user: Profile?, firstName: String, line: Line): Result<Unit>
    suspend fun joinTheQueue(line: Line, firstName: String? = null): SimpleResource
    suspend fun leaveTheQueue(queueItemId: String): SimpleResource
    suspend fun closeSession()
}