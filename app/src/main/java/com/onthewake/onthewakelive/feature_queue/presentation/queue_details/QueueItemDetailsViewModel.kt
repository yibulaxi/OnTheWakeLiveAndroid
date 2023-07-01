package com.onthewake.onthewakelive.feature_queue.presentation.queue_details

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.core.utils.Constants.DETAILS_ARGUMENT_KEY
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueItemDetailsViewModel @Inject constructor(
    private val queueService: QueueService,
    sharedPreferences: SharedPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(QueueItemDetailsState())
    val state: State<QueueItemDetailsState> = _state

    init {
        savedStateHandle.get<String>(DETAILS_ARGUMENT_KEY)?.let { queueItemId ->
            getQueueItemDetails(queueItemId)
        }
        _state.value = state.value.copy(
            currentUserId = sharedPreferences.getString(Constants.PREFS_USER_ID, null)
        )
    }

    private fun getQueueItemDetails(queueItemId: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            when (val result = queueService.getQueueItemDetails(queueItemId)) {
                is Resource.Success -> {
                    result.data?.let {
                        _state.value = state.value.copy(
                            userId = it.userId,
                            firstName = it.firstName,
                            lastName = it.lastName,
                            phoneNumber = it.phoneNumber,
                            profilePictureUri = it.profilePictureUri,
                            instagram = it.instagram,
                            telegram = it.telegram,
                            dateOfBirth = it.dateOfBirth
                        )
                    }
                }
                is Resource.Error -> _state.value = state.value.copy(
                    error = result.message
                )
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }
}