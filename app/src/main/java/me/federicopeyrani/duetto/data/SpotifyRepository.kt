package me.federicopeyrani.duetto.data

import android.util.Log
import androidx.annotation.IntRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import me.federicopeyrani.spotify_web_api.objects.PlayHistoryObject
import me.federicopeyrani.spotify_web_api.services.WebService
import me.federicopeyrani.spotify_web_api.services.WebService.Companion.getArtists
import me.federicopeyrani.spotify_web_api.services.WebService.TimeRange
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepository @Inject constructor(
    private val webService: WebService,
    private val trackDao: TrackDao,
    private val playHistoryDao: PlayHistoryDao
) {

    companion object {
        private const val CURRENT_PLAYBACK_POLLING_INTERVAL_MS = 10000L
    }

    private val context = Dispatchers.IO

    fun getCurrentPlayback(): Flow<CurrentPlaybackObject?> = flow {
        while (true) {
            try {
                val currentPlaybackObject = webService.getCurrentPlayback()
                emit(currentPlaybackObject)
            } catch (e: KotlinNullPointerException) {
                // when no item is being played, a KotlinNullPointerException is thrown because
                // the body of the response is empty
                Log.d("SpotifyRepository", "No item playing")
                emit(null)
            } catch (e: HttpException) {
                // generic HttpExceptions, such as an invalid login
                Log.d("SpotifyRepository", "getCurrentPlayback() failed, retrying.")
            }

            // delay next polling by CURRENT_PLAYBACK_POLLING_INTERVAL_MS
            delay(CURRENT_PLAYBACK_POLLING_INTERVAL_MS)
        }
    }.flowOn(context)

    fun getTrack(trackId: String): Flow<Track> = flow {
        emit(webService.getTrack(trackId).toTrack())
    }

    suspend fun getTopTracks(
        timeRange: TimeRange,
        @IntRange(from = 1, to = 50) limit: Int = 20
    ) = webService.getTopTracks(timeRange, limit).items

    suspend fun getTopArtists(
        timeRange: TimeRange,
        @IntRange(from = 1, to = 50) limit: Int = 20
    ) = webService.getTopArtists(timeRange, limit).items

    suspend fun getTopGenres(timeRange: TimeRange): Map<String, Int> {
        val topArtistsByTracks = getTopTracks(timeRange).flatMap { it.artists }
        return webService.getArtists(topArtistsByTracks)
            .flatMap { it.genres?.toList() ?: emptyList() }
            .groupingBy { it }
            .eachCount()
    }

    fun getPlayHistoryPagingSource() = playHistoryDao.getPlayHistoryPagingSource()

    suspend fun updatePlayHistory() {
        val playHistoryObjects = webService.getRecentlyPlayedTracks(50).items
        val playHistoryItems = playHistoryObjects.map(PlayHistoryObject::toPlayHistory)

        trackDao.insertAll(playHistoryItems.map { it.track })
        playHistoryDao.insertAll(playHistoryItems.map(PlayHistory::toPlayHistoryEntity))
    }
}