package me.federicopeyrani.duetto.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.federicopeyrani.spotify_web_api.objects.ImageObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject

fun TrackObject.toTrack() = Track(
    id = id,
    title = name,
    artist = artists.joinToString(", ") { it.name },
    albumArtUrls = album.images
)

@Entity(tableName = "tracks")
class Track(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    val title: String,
    val artist: String,
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