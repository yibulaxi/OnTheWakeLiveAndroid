package com.onthewake.onthewakelive.di

import android.content.SharedPreferences
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.feature_queue.data.remote.*
import com.onthewake.onthewakelive.feature_queue.data.repository.QueueServiceImpl
import com.onthewake.onthewakelive.feature_queue.data.repository.QueueSocketServiceImpl
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueService
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QueueModule {

    @Provides
    @Singleton
    fun provideHttpClient(prefs: SharedPreferences): HttpClient = HttpClient(CIO) {
        install(Logging)
        install(WebSockets)
        install(ContentNegotiation) { json() }
        install(DefaultRequest) {
            val token = prefs.getString(Constants.PREFS_JWT_TOKEN, null)
            header("Authorization", "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    @Provides
    @Singleton
    fun provideQueueApi(client: OkHttpClient): QueueApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(QueueApi::class.java)

    @Provides
    @Singleton
    fun provideQueueService(queueApi: QueueApi): QueueService =
        QueueServiceImpl(queueApi = queueApi)

    @Provides
    fun provideQueueSocketService(
        client: HttpClient,
        sharedPreferences: SharedPreferences
    ): QueueSocketService = QueueSocketServiceImpl(
        client = client,
        sharedPreferences = sharedPreferences
    )
}