package me.federicopeyrani.duetto.data

import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import me.federicopeyrani.spotify_web_api.services.LyricsService
import me.federicopeyrani.spotify_web_api.services.WebService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepository @Inject constructor(
    private val webService: WebService,
    private val lyricsService: LyricsService,
) {

    suspend fun getCurrentPlayback(): CurrentPlaybackObject = webService.getCurrentPlayback()
}