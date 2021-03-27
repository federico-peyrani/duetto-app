package me.federicopeyrani.duetto.data

import androidx.room.Embedded
import androidx.room.Ignore
import me.federicopeyrani.spotify_web_api.objects.PlayHistoryObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal val dateFormat
    get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

fun PlayHistoryObject.toPlayHistory() = PlayHistory(
    playedAt = dateFormat.parse(playedAt)!!,
    track = track.toTrack()
)

data class PlayHistory(
    val playedAt: Date,
    @Embedded val track: Track
) {

    @Ignore
    val playedAtString = playedAt.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayHistory

        if (playedAt != other.playedAt) return false

        return true
    }

    override fun hashCode(): Int {
        return playedAt.hashCode()
    }
}