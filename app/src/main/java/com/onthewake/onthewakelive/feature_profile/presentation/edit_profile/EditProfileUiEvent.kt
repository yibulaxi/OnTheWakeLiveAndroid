package com.onthewake.onthewakelive.feature_profile.presentation.edit_profile

import android.net.Uri

sealed interface EditProfileUiEvent {
    data class FirstNameChanged(val value: String): EditProfileUiEvent
    data class LastNameChanged(val value: String): EditProfileUiEvent
    data class PhoneNumberChanged(val value: String): EditProfileUiEvent
    data class TelegramChanged(val value: String): EditProfileUiEvent
    data class InstagramChanged(val value: String): EditProfileUiEvent
    data class DateOfBirthChanged(val value: String): EditProfileUiEvent
    object EditProfile: EditProfileUiEvent
    data class CropImage(val uri: Uri?) : EditProfileUiEvent
}