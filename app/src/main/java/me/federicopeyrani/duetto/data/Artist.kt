package me.federicopeyrani.duetto.data

import me.federicopeyrani.spotify_web_api.objects.ArtistObject
import me.federicopeyrani.spotify_web_api.objects.ImageObject

fun ArtistObject.toArtist() = Artist(
    id = id,
    name = name,
    images = images
)

data class Artist(
    val id: String,
    val name: String,
    val images: Array<ImageObject>?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        if (id != other.id) return false
        if (name != other.name) return false
        if (images != null) {
            if (other.images == null) return false
            if (!images.contentEquals(other.images)) return false
        } else if (other.images != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (images?.contentHashCode() ?: 0)
        return result
    }
}