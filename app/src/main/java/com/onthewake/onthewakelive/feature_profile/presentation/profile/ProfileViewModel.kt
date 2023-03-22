package com.onthewake.onthewakelive.feature_profile.presentation.profile

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.presentation.dataStore
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.UserProfileSerializer.defaultValue
import com.onthewake.onthewakelive.core.utils.put
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val context: Application,
    private val prefs: SharedPreferences
) : AndroidViewModel(context) {

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    val isLoading = mutableStateOf(false)

    fun getProfile() {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = profileRepository.getProfile()) {
                is Resource.Success -> {
                    result.data?.let {
                        // TODO remove context from view model!
                        context.dataStore.updateData { profile ->
                            profile.copy(
                                firstName = it.firstName,
                                lastName = it.lastName,
                                phoneNumber = it.phoneNumber,
                                instagram = it.instagram,
                                telegram = it.telegram,
                                dateOfBirth = it.dateOfBirth,
                                profilePictureUri = it.profilePictureUri
                            )
                        }
                    }
                }
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
            isLoading.value = false
        }
    }

    // TODO transfer this logic to repo
    fun logout() {
        prefs.put(Constants.PREFS_JWT_TOKEN, null)
        // TODO remove context from view model!
        viewModelScope.launch {
            context.dataStore.updateData { defaultValue }
        }
    }
}