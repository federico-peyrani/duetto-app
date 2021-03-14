package me.federicopeyrani.duetto.data

import me.federicopeyrani.spotify_web_api.objects.ImageObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject

fun TrackObject.toTrack() = Track(
    title = name,
    artist = artists.joinToString(", ") { it.name },
    albumArtUrls = album.images
)

class Track(
    val title: String,
    val artist: String,
    val albumArtUrls: Array<ImageObject>,
)