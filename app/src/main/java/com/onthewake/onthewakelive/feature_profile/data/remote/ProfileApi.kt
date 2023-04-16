package com.onthewake.onthewakelive.feature_profile.data.remote

import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData
import retrofit2.http.*

interface ProfileApi {

    @GET("/profile")
    suspend fun getProfile(): Profile

    @PUT("/update_profile")
    suspend fun updateProfile(
        @Body updateProfileData: UpdateProfileData
    )
}