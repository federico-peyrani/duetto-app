package me.federicopeyrani.duetto.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenInterceptor @Inject constructor(
    private val accessTokenRepository: AccessTokenRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = accessTokenRepository.accessToken
        val call = chain.request()
        val request = call.newBuilder().apply {
            header("Authorization", "Bearer $accessToken")
        }.build()
        return chain.proceed(request)
    }
}