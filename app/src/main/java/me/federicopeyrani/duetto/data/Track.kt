package me.federicopeyrani.duetto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.federicopeyrani.spotify_web_api.objects.ImageObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

fun TrackObject.toTrack() = Track(
    id = id,
    title = name,
    artist = artists.map { it.toArtist() }.toTypedArray(),
    album = album.name,
    duration = duration
        .toDuration(TimeUnit.MILLISECONDS)
        .toComponents { min, sec, _ -> "$min:${sec.toString().padStart(2, '0')}" },
    albumArtUrls = album.images
)

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: String,
    val title: String,
    val artist: Array<Artist>,
    val album: String,
    val duration: String,
    val albumArtUrls: Array<ImageObject>,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}