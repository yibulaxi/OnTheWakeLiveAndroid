package com.onthewake.onthewakelive.feature_queue.presentation.queue_list

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_FIRST_NAME
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_USER_ID
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_queue.domain.module.AddToQueueResult
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueService: QueueService,
    private val queueSocketService: QueueSocketService,
    prefs: SharedPreferences,
) : ViewModel() {

    private val _state = mutableStateOf(QueueState())
    val state: State<QueueState> = _state

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    var showDialog by mutableStateOf(false)

    val firstName = prefs.getString(PREFS_FIRST_NAME, null)
    val userId = prefs.getString(PREFS_USER_ID, null)

    fun connectToQueue() {
        getQueue()

        viewModelScope.launch {
            when (val result = queueSocketService.initSession(firstName!!)) {
                is Resource.Success -> observeQueue()
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
        }
    }

    private fun observeQueue() {
        queueSocketService.observeQueue()
            .onEach { queueItem ->
                val newList = state.value.queue
                    .toMutableList()
                    .apply {
                        if (queueItem.isDeleteAction) removeIf { it.id == queueItem.queueItem.id }
                        else add(0, queueItem.queueItem)
                    }
                    .sortedWith(compareByDescending { it.timestamp })
                _state.value = state.value.copy(queue = newList)
            }.launchIn(viewModelScope)
    }

    private fun getQueue() {
        viewModelScope.launch {
            _state.value = state.value.copy(isQueueLoading = true)
            when (val result = queueService.getQueue()) {
                is Resource.Success -> _state.value = state.value.copy(
                    queue = result.data ?: emptyList()
                )
                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
            _state.value = state.value.copy(isQueueLoading = false)
        }
    }

    private fun canAddToQueue(isLeftQueue: Boolean): AddToQueueResult {
        val allQueue = state.value.queue.sortedWith(compareBy { it.timestamp })
        val leftQueueItems = allQueue.filter { it.isLeftQueue }
        val rightQueueItems = allQueue.filter { !it.isLeftQueue }

        val isUserAlreadyInQueue = allQueue.find { it.userId == userId } != null
        val userItemInLeftQueue = leftQueueItems.find { it.userId == userId }
        val userItemInRightQueue = rightQueueItems.find { it.userId == userId }

        val userPositionInLeftQueue = leftQueueItems.indexOf(userItemInLeftQueue)
        val userPositionInRightQueue = rightQueueItems.indexOf(userItemInRightQueue)

        return if (userId in Constants.ADMIN_IDS) AddToQueueResult(successful = true)
        else if (!isUserAlreadyInQueue) AddToQueueResult(successful = true)
        else if (isLeftQueue && userItemInLeftQueue != null) {
            AddToQueueResult(error = UIText.StringResource(R.string.already_in_queue_error))
        } else if (!isLeftQueue && userItemInRightQueue != null) {
            AddToQueueResult(error = UIText.StringResource(R.string.already_in_queue_error))
        } else if (isLeftQueue && userItemInRightQueue != null) {
            if (leftQueueItems.size - userPositionInRightQueue >= 4) AddToQueueResult(successful = true)
            else if (userPositionInRightQueue - leftQueueItems.size >= 4) AddToQueueResult(
                successful = true
            )
            else AddToQueueResult(error = UIText.StringResource(R.string.interval_error))
        } else if (!isLeftQueue && userItemInLeftQueue != null) {
            if (rightQueueItems.size - userPositionInLeftQueue >= 4) AddToQueueResult(successful = true)
            else if (userPositionInLeftQueue - rightQueueItems.size >= 4) AddToQueueResult(
                successful = true
            )
            else AddToQueueResult(error = UIText.StringResource(R.string.interval_error))
        } else AddToQueueResult(error = UIText.StringResource(R.string.unknown_error))
    }

    // TODO bug fix
    fun addToQueue(isLeftQueue: Boolean, firstName: String? = null) {
        viewModelScope.launch {
            val canAddToQueueResult = canAddToQueue(isLeftQueue = isLeftQueue)
            if (!canAddToQueueResult.successful) {
                _snackBarEvent.emit(
                    canAddToQueueResult.error ?: UIText.StringResource(R.string.unknown_error)
                )
                return@launch
            }

            _state.value = state.value.copy(isQueueLoading = true)

            val result = queueSocketService.addToQueue(isLeftQueue, firstName)
            if (result is Resource.Error) _snackBarEvent.emit(result.message)

            _state.value = state.value.copy(isQueueLoading = false)
        }
    }

    fun deleteQueueItem(queueItemId: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(isQueueLoading = true)

            val result = queueService.deleteQueueItem(queueItemId)
            if (result is Resource.Error) _snackBarEvent.emit(result.message)

            _state.value = state.value.copy(isQueueLoading = false)
        }
    }


    fun disconnect() {
        viewModelScope.launch {
            queueSocketService.closeSession()
        }
    }
}