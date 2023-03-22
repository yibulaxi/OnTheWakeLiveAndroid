package com.onthewake.onthewakelive.feature_profile.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.onthewake.onthewakelive.core.utils.*
import com.onthewake.onthewakelive.di.AppModule.dataStore
import com.onthewake.onthewakelive.feature_profile.data.remote.ProfileApi
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val storage: FirebaseStorage,
    private val context: Context,
    private val prefs: SharedPreferences
) : ProfileRepository {

    override suspend fun getProfile(): Resource<Profile> = try {
        val response = profileApi.getProfile()

        context.dataStore.updateData { profile ->
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

        Resource.Success(response.toProfile())
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    override suspend fun updateProfile(
        updateProfileData: UpdateProfileData,
        selectedProfilePictureUri: Uri?
    ): SimpleResource = try {
        val uri = uploadToFirebaseStorage(
            storage = storage,
            selectedProfilePictureUri = selectedProfilePictureUri
        )
        profileApi.updateProfile(
            updateProfileData = UpdateProfileData(
                firstName = updateProfileData.firstName,
                lastName = updateProfileData.lastName,
                phoneNumber = updateProfileData.phoneNumber,
                instagram = updateProfileData.instagram,
                telegram = updateProfileData.telegram,
                dateOfBirth = updateProfileData.dateOfBirth,
                profilePictureUri = uri?.toString() ?: ""
            )
        )
        context.dataStore.updateData {
            it.copy(
                firstName = updateProfileData.firstName.trim(),
                lastName = updateProfileData.lastName.trim(),
                phoneNumber = updateProfileData.phoneNumber.trim(),
                instagram = updateProfileData.instagram.trim(),
                telegram = updateProfileData.telegram.trim(),
                dateOfBirth = updateProfileData.dateOfBirth.trim(),
                profilePictureUri = updateProfileData.profilePictureUri
            )
        }
        prefs.put(Constants.PREFS_FIRST_NAME, updateProfileData.firstName.trim())

        Resource.Success(Unit)
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    // TODO code refactor
    private suspend fun uploadToFirebaseStorage(
        storage: FirebaseStorage,
        selectedProfilePictureUri: Uri?
    ): Uri? = suspendCoroutine { continuation ->
        val child = prefs.getString(Constants.PREFS_USER_ID, null)

        // If profile image is blank - return null (when the user tries to change
        // profile data without profile image)
        if (child == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        val ref = storage.reference.child(child)

        if (selectedProfilePictureUri != null) {
            // Upload image to firebase storage and return url to save it on database
            ref.putFile(selectedProfilePictureUri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri -> continuation.resume(uri) }
            }
        } else {
            // If user didn't select new image, just return old picture url)
            ref.downloadUrl.addOnSuccessListener { uri -> continuation.resume(uri) }
        }
    }
}