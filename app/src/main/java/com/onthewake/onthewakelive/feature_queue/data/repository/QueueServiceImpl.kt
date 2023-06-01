package com.onthewake.onthewakelive.feature_queue.data.repository

import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.handleNetworkError
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.data.remote.QueueApi
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueueServiceImpl(
    private val queueApi: QueueApi
) : QueueService {

    override suspend fun getQueue(): Resource<List<QueueItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = queueApi.getQueue()
                Resource.Success(result)
            } catch (exception: Exception) {
                Resource.Error(handleNetworkError(exception))
            }
        }
    }

    override suspend fun getQueueItemDetails(queueItemId: String): Resource<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val response = queueApi.getProfileDetails(queueItemId = queueItemId)
                Resource.Success(response)
            } catch (exception: Exception) {
                Resource.Error(handleNetworkError(exception))
            }
        }
    }
}