package me.federicopeyrani.duetto.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(playHistories: List<PlayHistoryEntity>)

    @Transaction
    @Query("SELECT * FROM play_histories JOIN tracks ON trackId = id")
    fun getPlayHistories(): List<PlayHistory>

    @Query(
        """
            SELECT *
            FROM play_histories JOIN tracks ON trackId = id
            ORDER BY playedAt DESC
            """
    )
    fun getPlayHistoryPagingSource(): PagingSource<Int, PlayHistory>
}