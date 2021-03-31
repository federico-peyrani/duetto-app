package me.federicopeyrani.duetto.data

import androidx.room.Embedded
import me.federicopeyrani.spotify_web_api.objects.PlayHistoryObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.utcStringToDate(): Date? {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    parser.timeZone = TimeZone.getTimeZone("UTC")
    return parser.parse(this)
}

fun PlayHistoryObject.toPlayHistory() = PlayHistory(
    playedAt = playedAt.utcStringToDate()!!,
    track = track.toTrack()
)

data class PlayHistory(
    val playedAt: Date,
    @Embedded val track: Track
)