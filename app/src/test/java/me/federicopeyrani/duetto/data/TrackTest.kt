package me.federicopeyrani.duetto.data

import io.kotest.matchers.shouldBe
import me.federicopeyrani.spotify_web_api.objects.AlbumObject
import me.federicopeyrani.spotify_web_api.objects.TrackObject
import org.junit.Test

class TrackTest {

    @Test
    fun `given duration in milliseconds, should format duration as mm_ss`() {
        val duration = 181577

        val albumObject = AlbumObject("", "", "", "", arrayOf(), arrayOf(), arrayOf())
        val trackObject = TrackObject("", "", "", albumObject, listOf(), duration)
        val track = trackObject.toTrack()

        track.duration shouldBe "3:01"
    }
}