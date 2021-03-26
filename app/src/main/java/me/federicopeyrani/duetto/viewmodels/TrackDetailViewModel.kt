package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import me.federicopeyrani.duetto.data.SpotifyRepository

class TrackDetailViewModel @AssistedInject constructor(
    @Assisted private val trackId: String,
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {

    companion object {

        private val SHARING_STARTED = SharingStarted.WhileSubscribed()
    }

    @AssistedFactory
    interface Factory {
        fun create(trackId: String): TrackDetailViewModel
    }

    class FactoryImpl(
        private val assistedFactory: Factory,
        private val trackId: String
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return assistedFactory.create(trackId) as T
        }
    }

    val track = spotifyRepository.getTrack(trackId)
        .stateIn(viewModelScope, SHARING_STARTED, null)
}