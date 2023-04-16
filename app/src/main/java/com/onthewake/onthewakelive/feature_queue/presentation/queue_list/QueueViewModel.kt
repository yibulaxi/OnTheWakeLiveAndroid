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
import com.onthewake.onthewakelive.core.presentation.utils.UIText.StringResource
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_USER_ID
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_queue.domain.module.Action
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueService: QueueService,
    private val queueSocketService: QueueSocketService,
    prefs: SharedPreferences
) : ViewModel() {

    private val _state = mutableStateOf(QueueState())
    val state: State<QueueState> = _state

    var showDialog by mutableStateOf(false)

    val userId = prefs.getString(PREFS_USER_ID, null)

    init {
        if (!queueSocketService.isConnectionEstablished()) connectToQueue()
        else observeQueue()

        getQueue()
    }

    private fun connectToQueue() {
        viewModelScope.launch {
            when (val result = queueSocketService.initSession()) {
                is Resource.Success -> observeQueue()
                is Resource.Error -> _state.value = state.value.copy(error = result.message)
            }
        }
    }

    private fun observeQueue() {
        queueSocketService.observeQueue()
            .onEach { response ->
                val newList = state.value.queue
                    .toMutableList()
                    .apply {
                        if (response.action == Action.JOIN_THE_QUEUE) {
                            add(state.value.queue.size, response.queueItem)
                        } else {
                            removeIf { it == response.queueItem }
                        }
                    }
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

                is Resource.Error -> _state.value = state.value.copy(
                    error = result.message
                )
            }
            _state.value = state.value.copy(isQueueLoading = false)
        }
    }

    fun joinTheQueue(line: Line, firstName: String? = null) {
        canJoinTheQueue(
            line = line,
            onSuccess = {
                viewModelScope.launch {
                    queueSocketService.joinTheQueue(line = line, firstName = firstName)
                }
            },
            onError = { errorMessage ->
                _state.value = state.value.copy(error = errorMessage)
            }
        )
    }

    private fun canJoinTheQueue(
        line: Line,
        onSuccess: () -> Unit,
        onError: (UIText) -> Unit
    ) {
        val queue = state.value.queue
        val leftQueue = queue.filter { it.line == Line.LEFT }
        val rightQueue = queue.filter { it.line == Line.RIGHT }

        val isUserAlreadyInQueue = queue.find { it.userId == userId } != null
        val userItemInLeftQueue = leftQueue.find { it.userId == userId }
        val userItemInRightQueue = rightQueue.find { it.userId == userId }

        val userPositionInLeftQueue = leftQueue.indexOf(userItemInLeftQueue)
        val userPositionInRightQueue = rightQueue.indexOf(userItemInRightQueue)

        if (userId in Constants.ADMIN_IDS) onSuccess()
        else if (!isUserAlreadyInQueue) onSuccess()
        else if (line == Line.LEFT && userItemInLeftQueue != null)
            onError(StringResource(R.string.already_in_queue_error))
        else if (line == Line.RIGHT && userItemInRightQueue != null)
            onError(StringResource(R.string.already_in_queue_error))
        else if (line == Line.LEFT && userItemInRightQueue != null) {
            if (leftQueue.size - userPositionInRightQueue >= 4) onSuccess()
            else if (userPositionInRightQueue - leftQueue.size >= 4) onSuccess()
            else onError(StringResource(R.string.interval_error))
        } else if (line == Line.RIGHT && userItemInLeftQueue != null) {
            if (rightQueue.size - userPositionInLeftQueue >= 4) onSuccess()
            else if (userPositionInLeftQueue - rightQueue.size >= 4) onSuccess()
            else onError(StringResource(R.string.interval_error))
        } else onError(StringResource(R.string.unknown_error))
    }

    fun leaveTheQueue(queueItemId: String) {
        viewModelScope.launch {
            queueSocketService.leaveTheQueue(queueItemId)
        }
    }
}