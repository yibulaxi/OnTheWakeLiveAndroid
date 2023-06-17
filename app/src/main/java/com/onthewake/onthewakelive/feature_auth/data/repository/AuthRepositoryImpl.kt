package com.onthewake.onthewakelive.feature_auth.data.repository

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import com.onesignal.OneSignal
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_JWT_TOKEN
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_USER_ID
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.UserProfileSerializer
import com.onthewake.onthewakelive.core.utils.handleNetworkError
import com.onthewake.onthewakelive.core.utils.put
import com.onthewake.onthewakelive.feature_auth.data.remote.AuthApi
import com.onthewake.onthewakelive.feature_auth.data.remote.request.AuthRequest
import com.onthewake.onthewakelive.feature_auth.data.remote.request.CreateAccountRequest
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val dataStore: DataStore<Profile>,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    override suspend fun signIn(authRequest: AuthRequest): AuthResult = try {
        val response = api.signIn(request = authRequest)

        sharedPreferences.put(PREFS_JWT_TOKEN, response.token)
        sharedPreferences.put(PREFS_USER_ID, response.userId)

        AuthResult.Authorized
    } catch (exception: HttpException) {
        when (exception.code()) {
            401 -> AuthResult.Unauthorized
            409 -> AuthResult.IncorrectData
            else -> AuthResult.UnknownError
        }
    } catch (exception: Exception) {
        AuthResult.UnknownError
    }

    override suspend fun signUp(accountRequest: CreateAccountRequest): AuthResult = try {
        val result = api.signUp(request = accountRequest)

        OneSignal.setExternalUserId(result.userId)

        dataStore.updateData { profile ->
            profile.copy(
                firstName = accountRequest.firstName,
                lastName = accountRequest.lastName,
                phoneNumber = accountRequest.phoneNumber
            )
        }
        signIn(
            AuthRequest(
                phoneNumber = accountRequest.phoneNumber,
                password = accountRequest.password
            )
        )
    } catch (exception: HttpException) {
        when (exception.code()) {
            401 -> AuthResult.Unauthorized
            409 -> AuthResult.UserAlreadyExist
            else -> AuthResult.UnknownError
        }
    } catch (exception: Exception) {
        AuthResult.UnknownError
    }

    override suspend fun isUserAlreadyExists(phoneNumber: String): Resource<Boolean> = try {
        val isUserAlreadyExists = api.isUserAlreadyExists(phoneNumber)
        Resource.Success(isUserAlreadyExists)
    } catch (exception: Exception) {
        Resource.Error(handleNetworkError(exception))
    }

    override suspend fun authenticate(): AuthResult = try {
        val token = sharedPreferences.getString(PREFS_JWT_TOKEN, null) ?: AuthResult.Unauthorized
        api.authenticate("Bearer $token")
        AuthResult.Authorized
    } catch (exception: HttpException) {
        if (exception.code() == 401) AuthResult.Unauthorized else AuthResult.UnknownError
    } catch (exception: Exception) {
        AuthResult.UnknownError
    }

    override suspend fun logout() {
        sharedPreferences.edit().clear().apply()
        dataStore.updateData { UserProfileSerializer.defaultValue }
    }
}