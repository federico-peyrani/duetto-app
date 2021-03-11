package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentPlaybackObject(
    @SerialName("item") val item: TrackObject,
    @SerialName("progress_ms") val progress: Int,
)