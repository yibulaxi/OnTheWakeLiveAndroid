package com.onthewake.onthewakelive.feature_splash.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(SplashScreenState())
    val state: State<SplashScreenState> = _state

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val startDestination = when (repository.authenticate()) {
                AuthResult.Authorized -> Screen.QueueScreen
                AuthResult.Unauthorized -> Screen.LoginScreen
                else -> Screen.ServerUnavailableScreen
            }

            _state.value = state.value.copy(
                startDestination = startDestination,
                isLoading = false
            )
        }
    }
}