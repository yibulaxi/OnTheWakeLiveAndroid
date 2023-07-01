package com.onthewake.onthewakelive.feature_queue.data.remote

import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem
import retrofit2.http.GET
import retrofit2.http.Query

interface QueueApi {

    @GET("/queue")
    suspend fun getQueue(): List<QueueItem>

    @GET("/queue_item_details")
    suspend fun getProfileDetails(
        @Query("queueItemId") queueItemId: String
    ): Profile

    @GET("/search_users")
    suspend fun searchUsers(
        @Query("search_query") searchQuery: String
    ): List<Profile>
}
