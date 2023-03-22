package com.onthewake.onthewakelive.feature_profile.presentation.edit_profile

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.dataStore
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_FIRST_NAME
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.put
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import com.onthewake.onthewakelive.feature_profile.domain.module.UpdateProfileData
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val profileRepository: ProfileRepository,
    private val context: Application,
    private val prefs: SharedPreferences
) : AndroidViewModel(context) {

    var state by mutableStateOf(EditProfileState())

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    private val _navigateUp = MutableSharedFlow<Boolean>()
    val navigateUp = _navigateUp.asSharedFlow()

    private val _selectedProfilePictureUri = mutableStateOf<Uri?>(null)
    val selectedProfilePictureUri: State<Uri?> = _selectedProfilePictureUri

    init {
        updateDataFromDataStore()
    }

    fun onEvent(event: EditProfileUiEvent) {
        when (event) {
            is EditProfileUiEvent.EditProfileFirstNameChanged -> {
                state = state.copy(firstName = event.value)
            }
            is EditProfileUiEvent.EditProfileLastNameChanged -> {
                state = state.copy(lastName = event.value)
            }
            is EditProfileUiEvent.EditProfilePhoneNumberChanged -> {
                state = state.copy(phoneNumber = event.value)
            }
            is EditProfileUiEvent.EditProfileTelegramChanged -> {
                state = state.copy(telegram = event.value)
            }
            is EditProfileUiEvent.EditProfileInstagramChanged -> {
                state = state.copy(instagram = event.value)
            }
            is EditProfileUiEvent.EditProfileDateOfBirthChanged -> {
                state = state.copy(dateOfBirth = event.value)
            }
            is EditProfileUiEvent.CropImage -> {
                _selectedProfilePictureUri.value = event.uri
                state = state.copy(profilePictureUri = event.uri.toString())
            }
            is EditProfileUiEvent.EditProfile -> editProfile()
        }
    }

    private fun updateDataFromDataStore() {

        // TODO remove context from viewModel
        viewModelScope.launch {
            context.dataStore.data.collectLatest { profile ->
                state = state.copy(
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    phoneNumber = profile.phoneNumber,
                    telegram = profile.telegram,
                    instagram = profile.instagram,
                    dateOfBirth = profile.dateOfBirth,
                    profilePictureUri = profile.profilePictureUri
                )
            }
        }
    }

    private fun editProfile() {
        val firstNameResult = validationUseCase.validateFirstName(state.firstName)
        val lastNameResult = validationUseCase.validateLastName(state.lastName)
        val phoneNumberResult = validationUseCase.validatePhoneNumber(state.phoneNumber)
        val dateOfBirthResult = validationUseCase.validateDateOfBirth(state.dateOfBirth)

        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            phoneNumberResult,
            dateOfBirthResult
        ).any { !it.successful }

        if (hasError) {
            state = state.copy(
                profileFirsNameError = firstNameResult.errorMessage,
                profileLastNameError = lastNameResult.errorMessage,
                profilePhoneNumberError = phoneNumberResult.errorMessage,
                profileDateOfBirthError = dateOfBirthResult.errorMessage
            )
            return
        } else state = state.copy(
            profileFirsNameError = null,
            profileLastNameError = null,
            profilePhoneNumberError = null,
            profileDateOfBirthError = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)

            val result = profileRepository.updateProfile(
                updateProfileData = UpdateProfileData(
                    firstName = state.firstName.trim(),
                    lastName = state.lastName.trim(),
                    phoneNumber = state.phoneNumber.trim(),
                    instagram = state.instagram.trim(),
                    telegram = state.telegram.trim(),
                    dateOfBirth = state.dateOfBirth.trim(),
                    profilePictureUri = state.profilePictureUri
                ),
                selectedProfilePictureUri = selectedProfilePictureUri.value
            )

            _selectedProfilePictureUri.value = null
            prefs.put(PREFS_FIRST_NAME, state.firstName.trim())

            state = state.copy(isLoading = false)

            when (result) {
                is Resource.Success -> _snackBarEvent.emit(
                    UIText.StringResource(R.string.successfully_updated_profile)
                )
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
        }
    }
}