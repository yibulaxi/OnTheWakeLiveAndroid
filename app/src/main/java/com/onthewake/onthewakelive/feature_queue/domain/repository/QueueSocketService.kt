package com.onthewake.onthewakelive.feature_queue.domain.repository

import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.SimpleResource
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueResponse
import kotlinx.coroutines.flow.Flow

interface QueueSocketService {
    suspend fun initSession(firstName: String): Resource<Unit>
    fun observeQueue(): Flow<QueueResponse>
    suspend fun addToQueue(isLeftQueue: Boolean, firstName: String?): SimpleResource
    suspend fun closeSession()
}