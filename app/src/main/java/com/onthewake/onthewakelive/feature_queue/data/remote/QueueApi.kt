package com.onthewake.onthewakelive.feature_queue.data.remote

import com.onthewake.onthewakelive.feature_profile.data.remote.response.ProfileResponse
import com.onthewake.onthewakelive.feature_queue.data.remote.dto.QueueDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface QueueApi {

    @GET("/queue")
    suspend fun getQueue(): List<QueueDto>

    @GET("/queue_item/details")
    suspend fun getProfileDetails(
        @Query("queueItemId") queueItemId: String
    ): ProfileResponse

    @DELETE("/queue_item/delete")
    suspend fun deleteQueueItem(
        @Query("queueItemId") queueItemId: String
    )
}
