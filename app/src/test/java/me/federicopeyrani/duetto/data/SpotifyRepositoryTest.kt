package me.federicopeyrani.duetto.data

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import me.federicopeyrani.duetto.util.enqueueEmptyResponse
import me.federicopeyrani.duetto.util.enqueueResponse
import me.federicopeyrani.spotify_web_api.RetrofitBaseClient
import me.federicopeyrani.spotify_web_api.create
import me.federicopeyrani.spotify_web_api.services.WebService
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

class SpotifyRepositoryTest {

    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder().build()

    private val retrofit = RetrofitBaseClient(
        mockWebServer.url("/").toString(),
        client
    )

    private val webService: WebService = retrofit.create()

    @Test
    fun `given current playback is not null, returns current playback`() {
        mockWebServer.enqueueResponse("current-playback.json")
        val currentPlayback = runBlocking { webService.getCurrentPlayback() }
        currentPlayback.item.name shouldBe "GLAM!"
    }

    @Test
    fun `given current playback is null, throws KotlinNullPointerException`() {
        mockWebServer.enqueueEmptyResponse()
        shouldThrowExactly<KotlinNullPointerException> {
            runBlocking { webService.getCurrentPlayback() }
        }
    }

    @Test
    fun `given requested range is medium term, request contains exactly 'medium_term'`() {
        mockWebServer.enqueueResponse("top-tracks.json")
        runBlocking { webService.getTopTracks(WebService.TimeRange.MEDIUM_TERM) }

        val recordedRequest = mockWebServer.takeRequest()
        val timeRangeQueryParam = recordedRequest.requestUrl!!.queryParameter("time_range")
        timeRangeQueryParam shouldBe "medium_term"
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}