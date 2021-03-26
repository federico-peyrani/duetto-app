package me.federicopeyrani.duetto.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.federicopeyrani.spotify_web_api.objects.ImageObject
import me.federicopeyrani.spotify_web_api.objects.PlayHistoryObject

fun PlayHistoryObject.toPlayHistory() = PlayHistory(
    playedAt = playedAt,
    id = track.id,
    title = track.name,
    artist = track.artists.joinToString(", ") { it.name },
    albumArtUrls = track.album.images
)

@Entity(tableName = "play_histories")
class PlayHistory(
    @PrimaryKey @ColumnInfo(name = "played_at") val playedAt: String,
    val id: String,
    val title: String,
    val artist: String,
    val albumArtUrls: Array<ImageObject>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayHistory

        if (playedAt != other.playedAt) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playedAt.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}