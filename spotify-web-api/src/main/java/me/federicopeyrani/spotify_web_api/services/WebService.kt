package me.federicopeyrani.spotify_web_api.services

import me.federicopeyrani.spotify_web_api.objects.AlbumListObject
import me.federicopeyrani.spotify_web_api.objects.ArtistListObject
import me.federicopeyrani.spotify_web_api.objects.ArtistObject
import me.federicopeyrani.spotify_web_api.objects.CurrentPlaybackObject
import me.federicopeyrani.spotify_web_api.objects.PaginationObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    companion object : ServiceCompanionInterface<WebService> {
        override val baseUrl = "https://api.spotify.com/v1/"
        override val clazz = WebService::class.java

        // region Additional methods

        suspend fun WebService.getArtists(artists: List<ArtistObject>): ArtistListObject =
            getArtists(artists.joinToString(",") { it.id })

        // endregion
    }

    enum class TimeRange(private val toString: String) {
        SHORT_TERM("short_term"),
        MEDIUM_TERM("medium_term"),
        LONG_TERM("long_term");

        override fun toString(): String = toString
    }

    /**
     * Get information about the user’s current playback state, including track or episode,
     * progress, and active device.
     */
    @GET("me/player")
    suspend fun getCurrentPlayback(): CurrentPlaybackObject

    /**
     * Get Spotify catalog information for multiple albums identified by their Spotify IDs.
     *
     * @param albumIds comma-separated list of the Spotify IDs for the albums. Maximum: 20 IDs.
     */
    @GET("albums")
    suspend fun getAlbums(@Query("ids") albumIds: String): AlbumListObject

    /**
     * Get Spotify catalog information for several artists based on their Spotify IDs.
     *
     * @param artistIds comma-separated list of the Spotify IDs for the artists. Maximum: 50 IDs.
     */
    @GET("artists")
    suspend fun getArtists(@Query("ids") artistIds: String): ArtistListObject

    /**
     * Get the current user’s top artists or tracks based on calculated affinity.
     */
    @GET("me/top/artists")
    suspend fun getTopArtists(
        @Query("time_range") timeRange: TimeRange,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PaginationObject<ArtistObject>

    /**
     * Get the current user’s top artists or tracks based on calculated affinity.
     */
    @GET("me/top/tracks")
    suspend fun getTopTracks(
        @Query("time_range") timeRange: TimeRange,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PaginationObject<TrackObject>
}