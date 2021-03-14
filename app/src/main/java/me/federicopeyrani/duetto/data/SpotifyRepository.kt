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
        private const val CURRENT_PLAYBACK_POLLING_INTERVAL_MS = 10000L
    }

    private val context = Dispatchers.IO

    fun getCurrentPlayback(): Flow<CurrentPlaybackObject> = flow {
        while (true) {
            try {
                val currentPlaybackObject = webService.getCurrentPlayback()
                emit(currentPlaybackObject)
            } catch (e: KotlinNullPointerException) {
                // when no item is being played, a KotlinNullPointerException is thrown because
                // the body of the response is empty
                Log.d("SpotifyRepository", "No item playing")
            } catch (e: HttpException) {
                // generic HttpExceptions, such as an invalid login
                Log.d("SpotifyRepository", "getCurrentPlayback() failed, retrying.")
            }

            // delay next polling by CURRENT_PLAYBACK_POLLING_INTERVAL_MS
            delay(CURRENT_PLAYBACK_POLLING_INTERVAL_MS)
        }
    }.flowOn(context)
}