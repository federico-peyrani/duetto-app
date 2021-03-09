package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("ArrayInDataClass")
@Serializable
data class AlbumObject(
    @SerialName("uri") val uri: String,
    @SerialName("id") val id: String,
    @SerialName("album_type") val albumType: String,
    @SerialName("name") val name: String,
    @SerialName("images") val images: Array<ImageObject>,
    @SerialName("artists") val artists: Array<ArtistObject>,
    @SerialName("genres") val genres: Array<String>? = null,
)