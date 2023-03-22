package com.onthewake.onthewakelive.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import coil.ImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.onthewake.onthewakelive.core.utils.Constants.PREFS_JWT_TOKEN
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient =
        OkHttpClient.Builder().addInterceptor {
            val token = sharedPreferences.getString(PREFS_JWT_TOKEN, "")
            val modifiedRequest = it.request().newBuilder()
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
    fun provideValidateUseCase(): ValidationUseCase = ValidationUseCase()

    @Provides
    @Singleton
    fun provideImageLoader(app: Application): ImageLoader =
        ImageLoader.Builder(app)
            .crossfade(true)
            .build()

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences =
        app.getSharedPreferences("prefs", MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}