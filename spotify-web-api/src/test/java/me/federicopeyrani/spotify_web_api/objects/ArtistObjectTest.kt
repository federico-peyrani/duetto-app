package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class ArtistObjectTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `decode from json`() = assertDoesNotThrow {
        val content = this.javaClass.getResource("/objects/artist.json").readText()
        val artist: ArtistObject = json.decodeFromString(content)

        assert(artist.images != null)
        assert(artist.genres != null)
    }
}