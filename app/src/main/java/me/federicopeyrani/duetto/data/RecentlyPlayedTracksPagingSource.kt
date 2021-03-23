package me.federicopeyrani.duetto.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.federicopeyrani.spotify_web_api.objects.PlayHistoryObject
import me.federicopeyrani.spotify_web_api.services.WebService
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class RecentlyPlayedTracksPagingSource @Inject constructor(
    private val webService: WebService
) : PagingSource<Long, PlayHistory>() {

    override fun getRefreshKey(state: PagingState<Long, PlayHistory>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, PlayHistory> = try {
        val position = params.key
        val pagination = webService.getRecentlyPlayedTracks(params.loadSize, before = position)
        LoadResult.Page(
            data = pagination.items.map(PlayHistoryObject::toPlayHistory),
            prevKey = if (position == null) null else pagination.cursors?.after,
            nextKey = pagination.cursors?.before
        )
    } catch (exception: IOException) {
        Log.e("PagingSource", exception.cause?.message ?: "null")
        LoadResult.Error(exception)
    } catch (exception: HttpException) {
        Log.e("PagingSource", exception.cause?.message ?: "null")
        LoadResult.Error(exception)
    }
}