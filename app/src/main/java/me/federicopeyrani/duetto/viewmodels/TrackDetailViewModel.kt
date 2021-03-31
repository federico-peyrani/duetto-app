package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Target
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import me.federicopeyrani.duetto.adapters.PaletteGenerator
import me.federicopeyrani.duetto.data.SpotifyRepository

class TrackDetailViewModel @AssistedInject constructor(
    @Assisted private val trackId: String,
    private val spotifyRepository: SpotifyRepository,
    private val paletteGenerator: PaletteGenerator
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

    private val trackAlbumArtUrl = track
        .mapNotNull { track -> track?.albumArtUrls?.maxByOrNull { it.width }?.url }
        .distinctUntilChanged()

    val bitmap = trackAlbumArtUrl
        .map { paletteGenerator.loadBitmap(it) }
        .stateIn(viewModelScope, SHARING_STARTED, null)

    private val palette = bitmap
        .filterNotNull()
        .map { paletteGenerator.generatePalette(it, Target.VIBRANT) }
        .shareIn(viewModelScope, SHARING_STARTED)

    val mainContentSwatch = palette
        .map { it.dominantSwatch }
        .stateIn(viewModelScope, SHARING_STARTED, null)

    val artistChipSwatch = palette
        .map { it.vibrantSwatch }
        .stateIn(viewModelScope, SHARING_STARTED, null)
}