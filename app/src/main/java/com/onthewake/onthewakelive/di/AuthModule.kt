package com.onthewake.onthewakelive.di

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import com.google.firebase.auth.FirebaseAuth
import com.onthewake.onthewakelive.core.utils.Constants.BASE_URL
import com.onthewake.onthewakelive.feature_auth.data.remote.AuthApi
import com.onthewake.onthewakelive.feature_auth.data.repository.AuthRepositoryImpl
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(client: OkHttpClient): AuthApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        firebaseAuth: FirebaseAuth,
        dataStore: DataStore<Profile>,
        sharedPreferences: SharedPreferences
    ): AuthRepository = AuthRepositoryImpl(
        api = api,
        firebaseAuth = firebaseAuth,
        dataStore = dataStore,
        sharedPreferences = sharedPreferences
    )
}