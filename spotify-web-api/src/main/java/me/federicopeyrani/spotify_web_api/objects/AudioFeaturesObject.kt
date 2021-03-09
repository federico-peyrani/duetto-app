package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioFeaturesObject(
    @SerialName("uri") val uri: String,
    @SerialName("id") val id: String,
    @SerialName("acousticness") val acousticness: Float,
    @SerialName("energy") val energy: Float,
    @SerialName("instrumentalness") val instrumentalness: Float,
    @SerialName("key") val key: Int,
    @SerialName("liveness") val liveness: Float,
    @SerialName("loudness") val loudness: Float,
    @SerialName("mode") val mode: Int,
    @SerialName("speechiness") val speechiness: Float,
    @SerialName("tempo") val tempo: Float,
    @SerialName("time_signature") val timeSignature: Int,
    @SerialName("valence") val valence: Float,
    @SerialName("danceability") val danceability: Float,
)