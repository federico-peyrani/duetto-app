package me.federicopeyrani.spotify_web_api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import me.federicopeyrani.spotify_web_api.services.ServiceCompanionInterface
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class RetrofitBaseClient<T>(
    private val serviceCompanion: ServiceCompanionInterface<T>,
    loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
    customInterceptor: Interceptor? = null,
) {

    companion object {
        const val CONTENT_TYPE_APPLICATION_JSON = "application/json"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = loggingLevel
    }

    private val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(loggingInterceptor)
        customInterceptor?.let { addInterceptor(it) }
    }.build()

    private val contentType = CONTENT_TYPE_APPLICATION_JSON.toMediaType()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder().apply {
        baseUrl(serviceCompanion.baseUrl)
        client(okHttpClient)
        addConverterFactory(json.asConverterFactory(contentType))
    }.build()

    fun create(): T = retrofit.create(serviceCompanion.clazz)
}