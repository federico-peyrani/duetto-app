package me.federicopeyrani.duetto.di.modules

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @Singleton
    fun provideOkHttpBaseClient(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        baseOkHttpClient: OkHttpClient
    ) = ImageLoader.Builder(context)
        .okHttpClient(baseOkHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideAuthService(baseOkHttpClient: OkHttpClient): AuthService {
        val retrofit = RetrofitBaseClient(AuthService.BASE_URL, baseOkHttpClient)
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideWebService(
        baseOkHttpClient: OkHttpClient,
        accessTokenInterceptor: AccessTokenInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
    ): WebService {
        val okHttpClient = baseOkHttpClient.newBuilder()
            .addInterceptor(accessTokenInterceptor)
            .authenticator(accessTokenAuthenticator)
            .build()
        val retrofit = RetrofitBaseClient(WebService.BASE_URL, okHttpClient)
        return retrofit.create()
    }
}