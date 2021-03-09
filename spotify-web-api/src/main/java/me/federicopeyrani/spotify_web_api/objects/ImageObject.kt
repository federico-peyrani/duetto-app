package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageObject(
    @SerialName("height") val height: Int,
    @SerialName("width") val width: Int,
    @SerialName("url") val url: String,
)