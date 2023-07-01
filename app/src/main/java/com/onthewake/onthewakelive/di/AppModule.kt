package com.onthewake.onthewakelive.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_JWT_TOKEN
import com.onthewake.onthewakelive.core.utils.Constants.SHARED_PREFS
import com.onthewake.onthewakelive.core.utils.UserProfileSerializer
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val Context.dataStore by dataStore(
        fileName = "user-profile.json",
        serializer = UserProfileSerializer
    )

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sharedPreferences: SharedPreferences
    ): OkHttpClient = OkHttpClient.Builder().addInterceptor {
        val token = sharedPreferences.getString(PREFS_JWT_TOKEN, null)
        val modifiedRequest = it.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        it.proceed(modifiedRequest)
    }.addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Profile> = context.dataStore

    @Provides
    @Singleton
    fun provideValidateUseCase(): ValidationUseCase = ValidationUseCase()

    @Provides
    @Singleton
    fun provideSharedPref(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
}