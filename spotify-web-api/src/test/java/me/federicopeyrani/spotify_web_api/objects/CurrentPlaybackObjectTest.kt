package me.federicopeyrani.spotify_web_api.objects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class CurrentPlaybackObjectTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `decode from json`() = org.junit.jupiter.api.assertDoesNotThrow {
        val content = this.javaClass.getResource("/objects/current-playback.json").readText()
        @Suppress("UNUSED_VARIABLE") val currentPlayback: CurrentPlaybackObject =
            json.decodeFromString(content)
    }
}