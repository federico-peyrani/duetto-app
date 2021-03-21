package me.federicopeyrani.spotify_web_api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

inline fun <reified T> RetrofitBaseClient.create() = create(T::class.java)

class RetrofitBaseClient(
    private val baseUrl: String,
    private val client: OkHttpClient,
) {

    companion object {
        const val CONTENT_TYPE_APPLICATION_JSON = "application/json"
    }

    private val contentType = CONTENT_TYPE_APPLICATION_JSON.toMediaType()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder().apply {
        baseUrl(baseUrl)
        client(client)
        addConverterFactory(json.asConverterFactory(contentType))
    }.build()

    fun <T> create(clazz: Class<T>): T = retrofit.create(clazz)
}