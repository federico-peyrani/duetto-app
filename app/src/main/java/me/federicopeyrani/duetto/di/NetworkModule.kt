package me.federicopeyrani.duetto.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.federicopeyrani.duetto.FirstLaunchActivity
import me.federicopeyrani.spotify_web_api.RetrofitBaseClient
import me.federicopeyrani.spotify_web_api.services.AuthService
import me.federicopeyrani.spotify_web_api.services.LyricsService
import me.federicopeyrani.spotify_web_api.services.WebService

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(FirstLaunchActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    @Provides
    fun provideAuthServiceRetrofit() = RetrofitBaseClient(AuthService)

    @Provides
    fun provideAuthService(retrofit: RetrofitBaseClient<AuthService>): AuthService =
        retrofit.create()

    @Provides
    fun provideWebServiceRetrofit(prefs: SharedPreferences) = RetrofitBaseClient(WebService) {
        val token = prefs.getString(FirstLaunchActivity.KEY_ACCESS_TOKEN, null)
        val call = it.request()
        val request = call.newBuilder().apply {
            addHeader("Authorization", "Bearer $token")
        }.build()
        it.proceed(request)
    }

    @Provides
    fun provideWebService(retrofit: RetrofitBaseClient<WebService>): WebService = retrofit.create()

    @Provides
    fun provideLyricsServiceRetrofit() = RetrofitBaseClient(LyricsService)

    @Provides
    fun provideLyricsService(retrofit: RetrofitBaseClient<LyricsService>): LyricsService =
        retrofit.create()
}