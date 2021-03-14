package me.federicopeyrani.duetto.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.federicopeyrani.duetto.data.AccessTokenAuthenticator
import me.federicopeyrani.duetto.data.AccessTokenInterceptor
import me.federicopeyrani.spotify_web_api.RetrofitBaseClient
import me.federicopeyrani.spotify_web_api.services.AuthService
import me.federicopeyrani.spotify_web_api.services.LyricsService
import me.federicopeyrani.spotify_web_api.services.WebService
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        val retrofit = RetrofitBaseClient(AuthService)
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideWebService(
        accessTokenInterceptor: AccessTokenInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
    ): WebService {
        val retrofit = RetrofitBaseClient(serviceCompanion = WebService,
                                          loggingLevel = HttpLoggingInterceptor.Level.BODY,
                                          customInterceptor = accessTokenInterceptor,
                                          customAuthenticator = accessTokenAuthenticator)
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideLyricsService(): LyricsService {
        val retrofit = RetrofitBaseClient(LyricsService)
        return retrofit.create()
    }
}