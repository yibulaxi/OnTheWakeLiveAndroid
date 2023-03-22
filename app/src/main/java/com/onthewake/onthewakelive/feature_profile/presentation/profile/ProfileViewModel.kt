package com.onthewake.onthewakelive.feature_profile.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    var isLoading by mutableStateOf(false)

    fun getProfile() {
        viewModelScope.launch {
            isLoading = true

            val result = profileRepository.getProfile()
            if (result is Resource.Error) _snackBarEvent.emit(result.message)

            isLoading = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}