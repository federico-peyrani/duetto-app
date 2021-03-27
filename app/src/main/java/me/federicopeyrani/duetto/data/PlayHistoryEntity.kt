package me.federicopeyrani.duetto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "play_histories")
data class PlayHistoryEntity(
    @PrimaryKey val playedAt: Date,
    val trackId: String
)