package com.onthewake.onthewakelive.feature_queue.domain.repository

import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

interface QueueService {
    suspend fun getQueue(): Resource<List<QueueItem>>
    suspend fun getQueueItemDetails(queueItemId: String): Resource<Profile>
}