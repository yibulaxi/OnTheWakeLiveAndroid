package com.onthewake.onthewakelive.feature_queue.data.repository

import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.handleNetworkError
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.data.remote.QueueApi
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService

class QueueServiceImpl(
    private val queueApi: QueueApi
) : QueueService {

    override suspend fun getQueue(): Resource<List<QueueItem>> = try {
        val result = queueApi.getQueue()
        Resource.Success(result)
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    override suspend fun getProfileDetails(queueItemId: String): Resource<Profile> = try {
        val response = queueApi.getProfileDetails(queueItemId = queueItemId)
        Resource.Success(response)
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }
}