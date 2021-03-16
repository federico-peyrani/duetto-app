package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ArtistListObject(
    @SerialName("artists") private val artists: List<ArtistObject>
) : List<ArtistObject> by artists