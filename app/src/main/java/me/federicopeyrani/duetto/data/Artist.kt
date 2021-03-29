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

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}