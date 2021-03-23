package me.federicopeyrani.duetto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import me.federicopeyrani.duetto.data.RecentlyPlayedTracksPagingSource
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val recentlyPlayedTracksPagingSource: RecentlyPlayedTracksPagingSource
) : ViewModel() {

    val recentlyPlayed = Pager(
        config = PagingConfig(10),
        pagingSourceFactory = ::recentlyPlayedTracksPagingSource
    ).flow.cachedIn(viewModelScope)
}