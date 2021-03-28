package me.federicopeyrani.duetto.data

import me.federicopeyrani.spotify_web_api.objects.ArtistObject

fun ArtistObject.toArtist() = Artist(
    id = id,
    name = name
)

data class Artist(
    val id: String,
    val name: String
)