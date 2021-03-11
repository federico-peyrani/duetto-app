package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TimestampObject(
    @SerialName("seconds") val seconds: Int,
    @SerialName("lyrics") val lyrics: String,
)