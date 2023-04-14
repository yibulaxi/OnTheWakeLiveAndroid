package com.onthewake.onthewakelive.feature_profile.data.repository

import android.content.SharedPreferences
import android.net.Uri
import androidx.datastore.core.DataStore
import com.google.firebase.storage.FirebaseStorage
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.*
import com.onthewake.onthewakelive.feature_profile.data.remote.ProfileApi
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val storage: FirebaseStorage,
    private val prefs: SharedPreferences,
    private val dataStore: DataStore<Profile>
) : ProfileRepository {

    override suspend fun getProfile(): Resource<Profile> {
        return try {
            val cachedProfile = dataStore.data.first()
            if (cachedProfile.firstName.isNotEmpty()) return Resource.Success(cachedProfile)

            val response = profileApi.getProfile()

            dataStore.updateData { profile ->
                profile.copy(
                    firstName = response.firstName,
                    lastName = response.lastName,
                    phoneNumber = response.phoneNumber,
                    instagram = response.instagram,
                    telegram = response.telegram,
                    dateOfBirth = response.dateOfBirth,
                    profilePictureUri = response.profilePictureUri
                )
            }
            Resource.Success(response)
        } catch (exception: Exception) {
            Resource.Error(handleNetworkError(exception))
        }
    }

    private fun haveSomethingToUpdate(
        oldProfileData: Profile, newProfileData: UpdateProfileData
    ): Boolean = !(oldProfileData.firstName == newProfileData.firstName &&
            oldProfileData.lastName == newProfileData.lastName &&
            oldProfileData.profilePictureUri == newProfileData.profilePictureUri &&
            oldProfileData.instagram == newProfileData.instagram &&
            oldProfileData.telegram == newProfileData.telegram &&
            oldProfileData.dateOfBirth == newProfileData.dateOfBirth)

    override suspend fun updateProfile(updateProfileData: UpdateProfileData): SimpleResource {
        return try {
            val oldProfileData = dataStore.data.first()
            val profilePictureUri =
                updateProfileData.profilePictureUri ?: oldProfileData.profilePictureUri

            val haveSomethingToUpdate = haveSomethingToUpdate(
                oldProfileData = oldProfileData,
                newProfileData = updateProfileData.copy(profilePictureUri = profilePictureUri)
            )
            if (!haveSomethingToUpdate) {
                return Resource.Error(UIText.StringResource(R.string.nothing_to_update))
            }

            profileApi.updateProfile(
                updateProfileData = updateProfileData.copy(
                    profilePictureUri = profilePictureUri
                )
            )
            dataStore.updateData {
                it.copy(
                    firstName = updateProfileData.firstName,
                    lastName = updateProfileData.lastName,
                    phoneNumber = updateProfileData.phoneNumber,
                    instagram = updateProfileData.instagram,
                    telegram = updateProfileData.telegram,
                    dateOfBirth = updateProfileData.dateOfBirth,
                    profilePictureUri = profilePictureUri
                )
            }
            Resource.Success(Unit)
        } catch (exception: Exception) {
            Resource.Error(handleNetworkError(exception))
        }
    }

    override suspend fun uploadUserAvatarToFirebaseStorage(
        profilePictureUri: Uri?
    ): Uri? = suspendCoroutine { continuation ->
        val child = prefs.getString(Constants.PREFS_USER_ID, null)

        if (child.isNullOrEmpty() || profilePictureUri == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        val ref = storage.reference.child(child)

        ref.putFile(profilePictureUri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri -> continuation.resume(uri) }
        }
    }
}