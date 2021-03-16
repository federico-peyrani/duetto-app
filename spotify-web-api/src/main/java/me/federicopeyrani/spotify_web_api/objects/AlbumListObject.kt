package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AlbumListObject(
    @SerialName("albums") private val albums: List<AlbumObject>
) : List<AlbumObject> by albums