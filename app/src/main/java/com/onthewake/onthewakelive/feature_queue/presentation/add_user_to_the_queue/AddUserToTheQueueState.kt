package com.onthewake.onthewakelive.feature_queue.presentation.add_user_to_the_queue

import com.onthewake.onthewakelive.feature_profile.domain.module.Profile

data class AddUserToTheQueueState(
    val searchedUsers: List<Profile> = emptyList(),
    val isUserSearching: Boolean = false
)