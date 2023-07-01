package com.onthewake.onthewakelive.feature_queue.presentation.add_user_to_the_queue

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserToTheQueueViewModel @Inject constructor(
    private val queueService: QueueService,
    private val queueSocketService: QueueSocketService
) : ViewModel() {

    private val _state = mutableStateOf(AddUserToTheQueueState())
    val state: State<AddUserToTheQueueState> = _state

    private var searchJob: Job? = null

    fun addUserToTheQueue(user: Profile?, firstName: String, line: Line) {
        viewModelScope.launch {
            queueSocketService.addUserToTheQueue(
                user = user,
                firstName = firstName,
                line = line
            )
        }
    }

    fun searchUser(searchQuery: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = state.value.copy(isUserSearching = true)
            if (searchQuery.isNotEmpty()) {
                delay(200L)
            }
            queueService.searchUsers(searchQuery = searchQuery).onSuccess { searchedUsers ->
                _state.value = state.value.copy(searchedUsers = searchedUsers)
            }
            _state.value = state.value.copy(isUserSearching = false)
        }
    }
}