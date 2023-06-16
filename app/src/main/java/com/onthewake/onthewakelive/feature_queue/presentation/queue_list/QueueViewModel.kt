package com.onthewake.onthewakelive.feature_queue.presentation.queue_list

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_USER_ID
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_queue.domain.module.Action
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
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
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state = mutableStateOf(QueueState())
    val state: State<QueueState> = _state

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    init {
        connectToQueue()
    }

    fun connectToQueue() {
        if (queueSocketService.isConnectionEstablished()) return
        getQueue()
        getUserId()

        viewModelScope.launch {
            when (val result = queueSocketService.initSession()) {
                is Resource.Success -> observeQueue()
                is Resource.Error -> _snackBarEvent.emit(result.message)
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

                is Resource.Error -> _snackBarEvent.emit(result.message)
            }
            _state.value = state.value.copy(isQueueLoading = false)
        }
    }

    fun joinTheQueue(line: Line, firstName: String? = null) {
        _state.value = state.value.copy(showLeaveQueueDialog = false)

        viewModelScope.launch {
            val canJoinTheQueue = queueSocketService.canJoinTheQueue(
                line = line, queue = state.value.queue
            )
            when (canJoinTheQueue) {
                is Resource.Success -> {
                    queueSocketService.joinTheQueue(line = line, firstName = firstName)
                }

                is Resource.Error -> {
                    _snackBarEvent.emit(canJoinTheQueue.message)
                }
            }
        }
    }

    fun searchUser(searchQuery: String) {
        viewModelScope.launch {
            queueService.searchUsers(searchQuery = searchQuery).onSuccess { searchedUsers ->
                _state.value = state.value.copy(searchedUsers = searchedUsers)
            }
        }
    }

    private fun getUserId() {
        val userId = sharedPreferences.getString(PREFS_USER_ID, null)
        _state.value = state.value.copy(userId = userId)
    }

    fun onSwipedToDelete(queueItemIdToDelete: String) {
        _state.value = state.value.copy(
            showLeaveQueueDialog = true,
            queueItemIdToDelete = queueItemIdToDelete
        )
    }

    fun hideLeaveQueueDialog() {
        _state.value = state.value.copy(showLeaveQueueDialog = false)
    }

    fun leaveTheQueue() {
        viewModelScope.launch {
            queueSocketService.leaveTheQueue(
                queueItemId = state.value.queueItemIdToDelete ?: return@launch
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.launch {
            queueSocketService.closeSession()
        }
    }
}