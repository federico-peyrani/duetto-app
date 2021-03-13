package me.federicopeyrani.spotify_web_api.services

import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import retrofit2.Response
import retrofit2.http.GET

interface WebService {

    companion object : ServiceCompanionInterface<WebService> {
        override val baseUrl = "https://api.spotify.com/v1/"
        override val clazz = WebService::class.java
    }

    @GET("me/player")
    suspend fun getCurrentPlayback(): Response<CurrentPlaybackObject>
}