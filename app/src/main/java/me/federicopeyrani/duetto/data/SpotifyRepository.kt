package me.federicopeyrani.duetto.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import me.federicopeyrani.spotify_web_api.services.LyricsService
import me.federicopeyrani.spotify_web_api.services.WebService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepository @Inject constructor(
    private val webService: WebService,
    private val lyricsService: LyricsService,
) {

    companion object {
        private const val CURRENT_PLAYBACK_POLLING_INTERVAL_MS = 5000L
    }

    fun getCurrentPlayback(): Flow<CurrentPlaybackObject?> = flow {
        while (true) {
            try {
                val response = webService.getCurrentPlayback()
                if (response.code() != 204) {
                    val currentPlaybackObject = response.body()!!
                    emit(currentPlaybackObject)
                } else {
                    emit(null)
                }
            } catch (e: HttpException) {
                Log.d("SpotifyRepository", "getCurrentPlayback() failed, retrying.")
            }
            delay(CURRENT_PLAYBACK_POLLING_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)
}