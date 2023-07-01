package com.onthewake.onthewakelive.feature_auth.domain.repository

import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.feature_auth.data.remote.request.AuthRequest
import com.onthewake.onthewakelive.feature_auth.data.remote.request.CreateAccountRequest
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult

interface AuthRepository {
    suspend fun authenticate(): AuthResult
    suspend fun signIn(authRequest: AuthRequest): AuthResult
    suspend fun signUp(accountRequest: CreateAccountRequest): AuthResult
    suspend fun isUserAlreadyExists(phoneNumber: String): Resource<Boolean>
    suspend fun logout()
}