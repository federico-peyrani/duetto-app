package me.federicopeyrani.duetto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

fun PlayHistory.toPlayHistoryEntity() = PlayHistoryEntity(
    playedAt = playedAt,
    trackId = track.id
)

@Entity(tableName = "play_histories")
data class PlayHistoryEntity(
    @PrimaryKey val playedAt: Date,
    val trackId: String
)