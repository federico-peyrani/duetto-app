package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CursorBasedPaginationObject<T>(
    @SerialName("items") val items: List<T>,
    @SerialName("cursors") val cursors: Cursors?
) {

    @Serializable
    class Cursors(val before: Long, val after: Long)
}