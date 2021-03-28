package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import me.federicopeyrani.duetto.data.SpotifyRepository
import me.federicopeyrani.duetto.utils.generatePalette
import me.federicopeyrani.duetto.utils.loadBitmap

class TrackDetailViewModel @AssistedInject constructor(
    @Assisted private val trackId: String,
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(trackId: String): TrackDetailViewModel
    }

    companion object {

        private val SHARING_STARTED = SharingStarted.WhileSubscribed()

        fun create(assistedFactory: Factory, trackId: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                assistedFactory.create(trackId) as T
        }
    }

    val track = spotifyRepository.getTrack(trackId)
        .stateIn(viewModelScope, SHARING_STARTED, null)

    val bitmap = track
        .mapNotNull { track -> track?.albumArtUrls?.maxByOrNull { it.width }?.url }
        .map { loadBitmap(it) }
        .stateIn(viewModelScope, SHARING_STARTED, null)

    val palette = bitmap
        .filterNotNull()
        .map { it.generatePalette()?.vibrantSwatch }
        .stateIn(viewModelScope, SHARING_STARTED, null)
}