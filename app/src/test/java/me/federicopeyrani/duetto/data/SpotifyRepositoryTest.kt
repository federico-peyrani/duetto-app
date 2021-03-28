package me.federicopeyrani.duetto.data

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.federicopeyrani.duetto.util.enqueueEmptyResponse
import me.federicopeyrani.duetto.util.enqueueResponse
import me.federicopeyrani.duetto.util.test
import me.federicopeyrani.spotify_web_api.RetrofitBaseClient
import me.federicopeyrani.spotify_web_api.create
import me.federicopeyrani.spotify_web_api.services.WebService
import me.federicopeyrani.spotify_web_api.services.WebService.TimeRange
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

    private val spotifyRepository = SpotifyRepository(webService, mockk(), mockk())

    @Test
    fun `given current playback is not null, returns current playback`() = test {
        mockWebServer.enqueueResponse("current-playback.json")
        val currentPlayback = webService.getCurrentPlayback()
        currentPlayback.item.name shouldBe "GLAM!"
    }

    @Test
    fun `given current playback is null, throws KotlinNullPointerException`() = test {
        mockWebServer.enqueueEmptyResponse()
        shouldThrowExactly<KotlinNullPointerException> { webService.getCurrentPlayback() }
    }

    @Test
    fun `given requested range is medium term, request contains exactly 'medium_term'`() {
        mockWebServer.enqueueResponse("top-tracks-50.json")
        runBlocking { webService.getTopTracks(TimeRange.MEDIUM_TERM) }

        val recordedRequest = mockWebServer.takeRequest()
        val timeRangeQueryParam = recordedRequest.requestUrl!!.queryParameter("time_range")
        timeRangeQueryParam shouldBe "medium_term"
    }

    @Test
    fun `given requested limit of tracks is 50, size of returned list should be 50`() = test {
        mockWebServer.enqueueResponse("top-tracks-50.json")
        val tracks = spotifyRepository.getTopTracks(TimeRange.MEDIUM_TERM, 50)

        tracks shouldHaveSize 50
        tracks.map { it.id }.shouldBeUnique()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}