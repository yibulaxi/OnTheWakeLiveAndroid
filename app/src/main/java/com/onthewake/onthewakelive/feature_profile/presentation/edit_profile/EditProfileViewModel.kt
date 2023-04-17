package com.onthewake.onthewakelive.feature_profile.presentation.edit_profile

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = mutableStateOf(EditProfileState())
    val state: State<EditProfileState> = _state

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    private val _navigateUp = MutableSharedFlow<Boolean>()
    val navigateUp = _navigateUp.asSharedFlow()

    private val _selectedProfilePictureUri = mutableStateOf<Uri?>(null)
    val selectedProfilePictureUri: State<Uri?> = _selectedProfilePictureUri

    init {
        getProfile()
    }

    fun onEvent(event: EditProfileUiEvent) {
        when (event) {
            is EditProfileUiEvent.FirstNameChanged -> {
                _state.value = state.value.copy(firstName = event.value)
            }
            is EditProfileUiEvent.LastNameChanged -> {
                _state.value = state.value.copy(lastName = event.value)
            }
            is EditProfileUiEvent.PhoneNumberChanged -> {
                _state.value = state.value.copy(phoneNumber = event.value)
            }
            is EditProfileUiEvent.TelegramChanged -> {
                _state.value = state.value.copy(telegram = event.value)
            }
            is EditProfileUiEvent.InstagramChanged -> {
                _state.value = state.value.copy(instagram = event.value)
            }
            is EditProfileUiEvent.DateOfBirthChanged -> {
                _state.value = state.value.copy(dateOfBirth = event.value)
            }
            is EditProfileUiEvent.CropImage -> {
                _selectedProfilePictureUri.value = event.uri
                _state.value = state.value.copy(profilePictureUri = event.uri.toString())
            }
            is EditProfileUiEvent.EditProfile -> editProfile()
        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            when (val result = profileRepository.getProfile()) {
                is Resource.Success -> result.data?.let { profile ->
                    _state.value = state.value.copy(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        phoneNumber = profile.phoneNumber,
                        instagram = profile.instagram,
                        telegram = profile.telegram,
                        dateOfBirth = profile.dateOfBirth,
                        profilePictureUri = profile.profilePictureUri
                    )
                }
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }

    private fun editProfile() {
        val firstNameResult = validationUseCase.validateFirstName(state.value.firstName)
        val lastNameResult = validationUseCase.validateLastName(state.value.lastName)
        val phoneNumberResult = validationUseCase.validatePhoneNumber(state.value.phoneNumber)

        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            phoneNumberResult
        ).any { !it.successful }

        if (hasError) {
            _state.value = state.value.copy(
                profileFirsNameError = firstNameResult.errorMessage,
                profileLastNameError = lastNameResult.errorMessage,
                profilePhoneNumberError = phoneNumberResult.errorMessage
            )
            return
        } else _state.value = state.value.copy(
            profileFirsNameError = null,
            profileLastNameError = null,
            profilePhoneNumberError = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = state.value.copy(isLoading = true)

            val downloadUrl = profileRepository.uploadUserAvatarToFirebaseStorage(
                profilePictureUri = selectedProfilePictureUri.value
            )
            val result = profileRepository.updateProfile(
                updateProfileData = UpdateProfileData(
                    firstName = state.value.firstName.trim(),
                    lastName = state.value.lastName.trim(),
                    phoneNumber = state.value.phoneNumber.trim(),
                    instagram = state.value.instagram.trim(),
                    telegram = state.value.telegram.trim(),
                    dateOfBirth = state.value.dateOfBirth,
                    profilePictureUri = downloadUrl?.toString()
                )
            )
            _selectedProfilePictureUri.value = null
            _state.value = state.value.copy(isLoading = false)

            when (result) {
                is Resource.Success -> _snackBarEvent.emit(
                    UIText.StringResource(R.string.successfully_updated_profile)
                )
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
        }
    }
}