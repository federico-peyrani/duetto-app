package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PaginationObject<T>(
    @SerialName("offset") val offset: Int,
    @SerialName("total") val total: Int,
    @SerialName("previous") val previous: String?,
    @SerialName("next") val next: String?,
    @SerialName("items") val items: List<T>
)