package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayHistoryObject(
    @SerialName("played_at") val playedAt: String,
    @SerialName("track") val track: TrackObject
)