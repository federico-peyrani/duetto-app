package me.federicopeyrani.duetto.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.federicopeyrani.duetto.network.AccessTokenAuthenticator
import me.federicopeyrani.duetto.network.AccessTokenInterceptor
import me.federicopeyrani.spotify_web_api.RetrofitBaseClient
import me.federicopeyrani.spotify_web_api.create
import me.federicopeyrani.spotify_web_api.services.AuthService
import me.federicopeyrani.spotify_web_api.services.WebService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
        val retrofit = RetrofitBaseClient(AuthService.BASE_URL, okHttpClient)
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideWebService(
        accessTokenInterceptor: AccessTokenInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
    ): WebService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(accessTokenInterceptor)
            .authenticator(accessTokenAuthenticator)
            .build()
        val retrofit = RetrofitBaseClient(WebService.BASE_URL, okHttpClient)
        return retrofit.create()
    }
}