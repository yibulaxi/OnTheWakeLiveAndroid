package com.onthewake.onthewakelive.feature_auth.data.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
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
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val dataStore: DataStore<Profile>,
    private val firebaseAuth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override suspend fun signIn(authRequest: AuthRequest): AuthResult = try {
        val response = api.signIn(request = authRequest)

        sharedPreferences.put(PREFS_JWT_TOKEN, response.token)
        sharedPreferences.put(PREFS_USER_ID, response.userId)

        OneSignal.setExternalUserId(response.userId)

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
        api.signUp(request = accountRequest)

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

    override suspend fun sendOtp(
        phoneNumber: String,
        isResendAction: Boolean,
        context: Context
    ): AuthResult = suspendCoroutine { continuation ->
        try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

                override fun onVerificationFailed(exception: FirebaseException) {
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        continuation.resume(AuthResult.OtpInvalidCredentials)
                    } else if (exception is FirebaseTooManyRequestsException) {
                        continuation.resume(AuthResult.OtpTooManyRequests)
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)

                    storedVerificationId = verificationId
                    resendToken = token

                    continuation.resume(AuthResult.OtpSentSuccess)
                }
            }

            if (isResendAction) {
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(55L, TimeUnit.SECONDS)
                    .setActivity(context as Activity)
                    .setCallbacks(callbacks)
                    .setForceResendingToken(resendToken)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(55L, TimeUnit.SECONDS)
                    .setActivity(context as Activity)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            continuation.resume(AuthResult.UnknownError)
        }
    }

    override suspend fun verifyOtp(otp: String): AuthResult = suspendCoroutine { continuation ->
        if (!this::storedVerificationId.isInitialized) {
            continuation.resume(AuthResult.IncorrectOtp)
            return@suspendCoroutine
        }
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
            continuation.resume(AuthResult.OtpVerified)
        }.addOnFailureListener {
            continuation.resume(AuthResult.IncorrectOtp)
        }
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