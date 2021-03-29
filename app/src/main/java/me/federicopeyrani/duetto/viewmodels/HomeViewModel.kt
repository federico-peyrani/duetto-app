package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import me.federicopeyrani.duetto.adapters.PaletteGenerator
import me.federicopeyrani.duetto.data.SpotifyRepository
import me.federicopeyrani.duetto.data.Track
import me.federicopeyrani.duetto.data.toArtist
import me.federicopeyrani.duetto.data.toTrack
import me.federicopeyrani.spotify_web_api.objects.ArtistObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject
import me.federicopeyrani.spotify_web_api.services.WebService.TimeRange
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val paletteGenerator: PaletteGenerator
) : ViewModel() {

    companion object {

        private val SHARING_STARTED = SharingStarted.WhileSubscribed()

        private const val TOP_N_GENRES = 6

        private const val TOP_N_TRACKS = 5

        private const val TOP_N_ARTISTS = 6
    }

    val currentPlayback: StateFlow<Track?> = spotifyRepository.getCurrentPlayback()
        .map { it?.item?.toTrack() }
        .stateIn(viewModelScope, SHARING_STARTED, null)

    val currentPlaybackBitmap = currentPlayback
        .mapNotNull { track -> track?.albumArtUrls?.maxByOrNull { it.width }?.url }
        .map { paletteGenerator.loadBitmap(it) }
        .stateIn(viewModelScope, SHARING_STARTED, null)

    val currentPlaybackSwatch = currentPlaybackBitmap
        .filterNotNull()
        .map { paletteGenerator.generatePalette(it).vibrantSwatch }
        .stateIn(viewModelScope, SHARING_STARTED, null)

    suspend fun getTopGenres() = spotifyRepository.getTopGenres(TimeRange.MEDIUM_TERM)
        .toList()
        .sortedByDescending(Pair<String, Int>::second)
        .take(TOP_N_GENRES)
        .toMap()

    suspend fun getTopTracks() = spotifyRepository.getTopTracks(TimeRange.SHORT_TERM, TOP_N_TRACKS)
        .map(TrackObject::toTrack)

    suspend fun getTopArtists() =
        spotifyRepository.getTopArtists(TimeRange.SHORT_TERM, TOP_N_ARTISTS)
            .map(ArtistObject::toArtist)
}