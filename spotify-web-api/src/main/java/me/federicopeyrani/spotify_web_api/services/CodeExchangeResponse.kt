package me.federicopeyrani.spotify_web_api.services

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CodeExchangeResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("scope") val scope: String,
    @SerialName("expires_in") val expiresIn: Int,
    /**
     * The new refresh token to be used to refresh the current token in the future,
     * once used, it becomes invalid.
     */
    @SerialName("refresh_token") val refreshToken: String,
)