package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.federicopeyrani.duetto.data.SpotifyRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
) : ViewModel() {

    val recentlyPlayed = Pager(
        config = PagingConfig(10),
        pagingSourceFactory = spotifyRepository::getPlayHistoryPagingSource
    ).flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch { spotifyRepository.updatePlayHistory() }
    }
}