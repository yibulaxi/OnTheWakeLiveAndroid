package com.onthewake.onthewakelive.feature_profile.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_profile.domain.module.ProfileState
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    fun getProfile() {
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
                is Resource.Error -> _state.value = state.value.copy(error = result.message)
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}