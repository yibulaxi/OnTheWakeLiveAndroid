package com.onthewake.onthewakelive.di

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import com.google.firebase.storage.FirebaseStorage
import com.onthewake.onthewakelive.core.utils.Constants.BASE_URL
import com.onthewake.onthewakelive.feature_profile.data.remote.ProfileApi
import com.onthewake.onthewakelive.feature_profile.data.repository.ProfileRepositoryImpl
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApi(client: OkHttpClient): ProfileApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileApi: ProfileApi,
        storage: FirebaseStorage,
        prefs: SharedPreferences,
        dataStore: DataStore<Profile>
    ): ProfileRepository = ProfileRepositoryImpl(profileApi, storage, prefs, dataStore)
}