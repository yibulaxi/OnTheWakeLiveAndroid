package com.onthewake.onthewakelive.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.storage.FirebaseStorage
import com.onthewake.onthewakelive.core.utils.Constants.BASE_URL
import com.onthewake.onthewakelive.feature_profile.data.remote.ProfileApi
import com.onthewake.onthewakelive.feature_profile.data.repository.ProfileRepositoryImpl
import com.onthewake.onthewakelive.feature_profile.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext context: Context,
        storage: FirebaseStorage,
        prefs: SharedPreferences
    ): ProfileRepository = ProfileRepositoryImpl(profileApi, storage, context, prefs)

}