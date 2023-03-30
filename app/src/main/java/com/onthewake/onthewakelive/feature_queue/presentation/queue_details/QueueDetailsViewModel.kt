package com.onthewake.onthewakelive.feature_queue.presentation.queue_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.utils.Constants.DETAILS_ARGUMENT_KEY
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueDetailsViewModel @Inject constructor(
    private val queueService: QueueService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(QueueItemDetailsState())
    val state: State<QueueItemDetailsState> = _state

    init {
        savedStateHandle.get<String>(DETAILS_ARGUMENT_KEY)?.let { queueItemId ->
            loadQueueItemDetails(queueItemId)
        }
    }

    private fun loadQueueItemDetails(queueItemId: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            when (val result = queueService.getProfileDetails(queueItemId)) {
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
                is Resource.Error -> _state.value = state.value.copy(error = result.message)
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }
}