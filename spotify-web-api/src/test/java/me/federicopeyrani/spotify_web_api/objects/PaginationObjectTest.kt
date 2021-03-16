package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class PaginationObjectTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `decode artists from json`() = assertDoesNotThrow {
        val content = this.javaClass.getResource("/objects/top-artists.json").readText()
        val artists: PaginationObject<ArtistObject> = json.decodeFromString(content)

        assert(artists.items.isNotEmpty())
    }

    @Test
    fun `decode tracks from json`() = assertDoesNotThrow {
        val content = this.javaClass.getResource("/objects/top-tracks.json").readText()
        val artists: PaginationObject<TrackObject> = json.decodeFromString(content)

        assert(artists.items.isNotEmpty())
    }
}