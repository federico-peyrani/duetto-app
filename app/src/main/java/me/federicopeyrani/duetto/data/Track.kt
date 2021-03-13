package me.federicopeyrani.duetto.data

import me.federicopeyrani.spotify_web_api.objects.TrackObject

fun TrackObject.toTrack() = Track(
    title = name,
    artist = artists.joinToString(", ") { it.name },
)

class Track(
    val title: String,
    val artist: String,
)