package me.federicopeyrani.duetto.data

import android.util.Log
import androidx.annotation.IntRange
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import me.federicopeyrani.spotify_web_api.objects.ArtistObject
import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import me.federicopeyrani.spotify_web_api.objects.PlayHistoryObject
import me.federicopeyrani.spotify_web_api.services.WebService
import me.federicopeyrani.spotify_web_api.services.WebService.Companion.getArtists
import me.federicopeyrani.spotify_web_api.services.WebService.Companion.getArtistsById
import me.federicopeyrani.spotify_web_api.services.WebService.TimeRange
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class SpotifyRepository @Inject constructor(
    private val webService: WebService,
    private val trackDao: TrackDao,
    private val playHistoryDao: PlayHistoryDao
) : CoroutineScope {

    companion object {
        private const val CURRENT_PLAYBACK_POLLING_INTERVAL_MS = 10000L
    }

    override val coroutineContext: CoroutineContext
        get() = CoroutineName("SpotifyRepositoryScope") + Dispatchers.IO

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("SpotifyRepository", "Caught $throwable")
    }

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
            }

            // delay next polling by CURRENT_PLAYBACK_POLLING_INTERVAL_MS
            delay(CURRENT_PLAYBACK_POLLING_INTERVAL_MS)
        }
    }.flowOn(coroutineContext)

    /**
     * Creates a flow that will simultaneously query the database and the network, having the
     * network taking precedence over the database. This means that if the network emits a value
     * before the database, the job associated with the database query will be cancelled and only
     * the value from the network will be returned. It can also happen that the database will return
     * its value first and the network will subsequently emit its value after it.
     */
    private fun getTrackMerging(trackId: String) = channelFlow {
        Log.d("SpotifyRepository", "$trackId: loading from network and database")

        supervisorScope {
            val loadFromDatabase = launch(coroutineExceptionHandler) {
                val track = trackDao.getTrack(trackId) ?: return@launch
                send(track)
                Log.d("SpotifyRepository", "$trackId: fetched from database")
            }
            val loadFromNetwork = launch {
                val track = webService.getTrack(trackId).toTrack()
                send(track)
                Log.d("SpotifyRepository", "$trackId: fetched from network")
                loadFromDatabase.cancel()
                trackDao.insert(track)
            }

            joinAll(loadFromDatabase, loadFromNetwork)
            if (loadFromDatabase.isCancelled && loadFromNetwork.isCancelled) {
                throw Exception()
            }
        }
    }

    /**
     * Returns a flow that will emit in order: (1) the first [Track] emitted from either the network
     * or the database, with the network taking precedence over the database, (2) a [Track] object
     * that will also include the fully detailed [Artist] object, obtained by querying the network,
     * (3) any other [Track] emitted by the database or network, now enriched with the detail
     * about the [Artist] obtained at (2).
     */
    fun getTrack(trackId: String) = channelFlow {
        Log.d("SpotifyRepository", "Getting track $trackId")
        var artists: Array<Artist>? = null
        getTrackMerging(trackId).collectLatest {
            if (artists == null) {
                send(it)
                artists = webService.getArtistsById(it.artist.map(Artist::id))
                    .map(ArtistObject::toArtist)
                    .toTypedArray()
            }
            send(it.copy(artist = artists!!))
        }
    }.flowOn(coroutineContext)

    suspend fun getAudioFeatures(trackId: String) =
        withContext(coroutineContext) { webService.getAudioFeatures(trackId) }

    suspend fun getTopTracks(
        timeRange: TimeRange,
        @IntRange(from = 1, to = 50) limit: Int = 20
    ) = withContext(coroutineContext) { webService.getTopTracks(timeRange, limit).items }

    suspend fun getTopArtists(
        timeRange: TimeRange,
        @IntRange(from = 1, to = 50) limit: Int = 20
    ) = withContext(coroutineContext) { webService.getTopArtists(timeRange, limit).items }

    suspend fun getTopGenres(timeRange: TimeRange) = withContext(coroutineContext) {
        val topArtistsByTracks = getTopTracks(timeRange).flatMap { it.artists }
        webService.getArtists(topArtistsByTracks)
            .flatMap { it.genres?.toList() ?: emptyList() }
            .groupingBy { it }
            .eachCount()
    }

    fun getPlayHistoryPagingSource() = playHistoryDao.getPlayHistoryPagingSource()

    suspend fun updatePlayHistory() = withContext(coroutineContext) {
        val playHistoryObjects = webService.getRecentlyPlayedTracks(50).items
        val playHistoryItems = playHistoryObjects.map(PlayHistoryObject::toPlayHistory)

        trackDao.insertAll(playHistoryItems.map { it.track })
        playHistoryDao.insertAll(playHistoryItems.map(PlayHistory::toPlayHistoryEntity))
    }
}