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
        if (title != other.title) return false
        if (!artist.contentEquals(other.artist)) return false
        if (album != other.album) return false
        if (duration != other.duration) return false
        if (!albumArtUrls.contentEquals(other.albumArtUrls)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.contentHashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + albumArtUrls.contentHashCode()
        return result
    }
}