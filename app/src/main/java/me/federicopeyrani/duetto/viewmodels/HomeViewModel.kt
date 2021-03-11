package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.federicopeyrani.duetto.data.SpotifyRepository
import me.federicopeyrani.duetto.data.Track
import me.federicopeyrani.spotify_web_api.objects.ArtistObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {

    private val currentPlayback: Flow<TrackObject> =
        flow {
            while (true) {
                val currentPlayback = spotifyRepository.getCurrentPlayback()
                emit(currentPlayback)
                delay(5000)
            }
        }.map { it.item }

    suspend fun getCurrentPlaybackFlow(): Track {
        val name = currentPlayback.map { it.name }.stateIn(viewModelScope)
        val artists = currentPlayback.map {
            it.artists.joinToString(", ", transform = ArtistObject::name)
        }.stateIn(viewModelScope)
        return Track(name, artists)
    }
}