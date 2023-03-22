package com.onthewake.onthewakelive.feature_profile.data.remote

import com.onthewake.onthewakelive.feature_profile.data.remote.response.ProfileResponse
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData
import retrofit2.http.*

interface ProfileApi {

    @GET("/api/user/profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("/api/user/update")
    suspend fun updateProfile(
        @Body updateProfileData: UpdateProfileData
    )
}