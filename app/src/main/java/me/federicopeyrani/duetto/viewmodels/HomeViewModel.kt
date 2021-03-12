package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.federicopeyrani.duetto.data.SpotifyRepository
import me.federicopeyrani.spotify_web_api.objects.TrackObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {

    val trackObject: StateFlow<TrackObject?> = spotifyRepository.getCurrentPlayback()
        .map { it.item }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}