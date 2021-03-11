package me.federicopeyrani.spotify_web_api.services

import me.federicopeyrani.spotify_web_api.objects.TimestampObject
import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsService {

    companion object : ServiceCompanionInterface<LyricsService> {
        override val baseUrl = "https://api.textyl.co/"
        override val clazz = LyricsService::class.java
    }

    @GET("api/lyrics")
    fun getLyrics(@Query("q") query: String): Array<TimestampObject>
}