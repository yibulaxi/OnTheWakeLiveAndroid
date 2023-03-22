package com.onthewake.onthewakelive.feature_queue.presentation.queue_details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants.DETAILS_ARGUMENT_KEY
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueDetailsViewModel @Inject constructor(
    private val queueService: QueueService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state = mutableStateOf(QueueItemDetailsState())

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    init {
        savedStateHandle.get<String>(DETAILS_ARGUMENT_KEY)?.let { queueItemId ->
            loadQueueItemDetails(queueItemId)
        }
    }

    private fun loadQueueItemDetails(queueItemId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true)
            when (val result = queueService.getProfileDetails(queueItemId)) {
                is Resource.Success -> {
                    result.data?.let {
                        state.value = state.value.copy(
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
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
            state.value = state.value.copy(isLoading = false)
        }
    }
}