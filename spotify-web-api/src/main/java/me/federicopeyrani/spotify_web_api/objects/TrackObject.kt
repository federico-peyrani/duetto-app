package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("ArrayInDataClass")
@Serializable
data class TrackObject(
    @SerialName("uri") val uri: String,
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("album") val album: AlbumObject,
    @SerialName("artists") val artists: List<ArtistObject>,
    @SerialName("duration_ms") val duration: Int,
)