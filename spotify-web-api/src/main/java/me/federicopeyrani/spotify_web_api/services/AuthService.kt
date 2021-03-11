package me.federicopeyrani.spotify_web_api.services

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    companion object : ServiceCompanionInterface<AuthService> {
        override val baseUrl = "https://accounts.spotify.com/"
        override val clazz = AuthService::class.java
    }

    @POST("api/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String = "authorization_code",
    ): CodeExchangeResponse

    @POST("api/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "refresh_token",
    ): CodeExchangeResponse
}