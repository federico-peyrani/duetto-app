package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("ArrayInDataClass")
@Serializable
data class ArtistObject(
    @SerialName("uri") val uri: String,
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("genres") val genres: Array<String>? = null,
    @SerialName("images") val images: Array<ImageObject>? = null,
)