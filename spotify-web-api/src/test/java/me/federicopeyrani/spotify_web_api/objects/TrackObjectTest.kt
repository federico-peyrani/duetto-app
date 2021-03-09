package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class TrackObjectTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `decode from json`() = assertDoesNotThrow {
        val content = this.javaClass.getResource("/objects/track.json").readText()
        @Suppress("UNUSED_VARIABLE") val track: TrackObject = json.decodeFromString(content)
    }
}