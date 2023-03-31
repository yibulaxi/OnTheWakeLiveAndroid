package com.onthewake.onthewakelive.feature_profile.domain.repository

import android.net.Uri
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.SimpleResource
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData

interface ProfileRepository {
    suspend fun getProfile(): Resource<Profile>
    suspend fun updateProfile(updateProfileData: UpdateProfileData): SimpleResource
    suspend fun uploadUserAvatarToFirebaseStorage(profilePictureUri: Uri?): Uri?
}