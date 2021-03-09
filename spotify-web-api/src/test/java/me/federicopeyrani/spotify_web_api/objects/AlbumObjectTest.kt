package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class AlbumObjectTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `decode from json`() = assertDoesNotThrow {
        val content = this.javaClass.getResource("/objects/album.json").readText()
        @Suppress("UNUSED_VARIABLE") val album: AlbumObject = json.decodeFromString(content)
    }
}