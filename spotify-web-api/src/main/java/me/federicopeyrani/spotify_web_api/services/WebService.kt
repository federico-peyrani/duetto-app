package me.federicopeyrani.spotify_web_api.services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET

interface WebService {

    companion object {

        lateinit var token: String

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val call = it.request()
                val request = call.newBuilder().apply {
                    addHeader("Authorization", "Bearer $token")
                }.build()
                it.proceed(request)
            }.build()

        private val contentType = "application/json".toMediaType()

        private val json = Json {
            ignoreUnknownKeys = true
        }

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        fun build(): WebService = retrofit.create()
    }

    @GET("me/player")
    suspend fun getCurrentPlayback(): CurrentPlaybackObject
}