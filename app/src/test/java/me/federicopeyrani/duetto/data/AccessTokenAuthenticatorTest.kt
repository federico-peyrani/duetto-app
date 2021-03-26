package me.federicopeyrani.duetto.data

import android.content.Context
import android.content.SharedPreferences
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.federicopeyrani.duetto.network.AccessTokenAuthenticator
import me.federicopeyrani.duetto.network.AccessTokenInterceptor
import me.federicopeyrani.duetto.network.AccessTokenRepository
import me.federicopeyrani.duetto.util.enqueueResponse
import me.federicopeyrani.spotify_web_api.RetrofitBaseClient
import me.federicopeyrani.spotify_web_api.create
import me.federicopeyrani.spotify_web_api.services.AuthService
import me.federicopeyrani.spotify_web_api.services.CodeExchangeResponse
import me.federicopeyrani.spotify_web_api.services.WebService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import java.util.UUID

internal class AccessTokenAuthenticatorTest {

    private val mSharedPrefs = mockk<SharedPreferences>().apply {
        val map = mutableMapOf<String, String>()

        val mSharedPrefsEditor = mockk<SharedPreferences.Editor> {

            every { putString(any(), any()) } answers {
                map[firstArg()] = secondArg()
                this@mockk
            }

            every { commit() } returns true
            every { apply() } just Runs
        }

        every { getString(any(), any()) } answers { map[firstArg()] ?: secondArg() }

        every { edit() } returns mSharedPrefsEditor
    }

    private val mContext = mockk<Context>().apply {
        every { getSharedPreferences(any(), 0) } returns mSharedPrefs
    }

    private val authService = mockk<AuthService>()

    private val accessTokenRepository = AccessTokenRepository(mContext)

    private val accessTokenAuthenticator = AccessTokenAuthenticator(
        accessTokenRepository,
        authService
    )

    private val accessTokenInterceptor = AccessTokenInterceptor(accessTokenRepository)

    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .addInterceptor(accessTokenInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        })
        .authenticator(accessTokenAuthenticator)
        .build()

    private val retrofit = RetrofitBaseClient(
        mockWebServer.url("/").toString(),
        client
    )

    private val webService: WebService = retrofit.create()

    private fun randomToken() = UUID.randomUUID().toString()

    @Test
    fun testing() {
        // setup
        val accessToken = randomToken()
        val refreshToken = randomToken()

        // given
        accessTokenRepository.putToken(accessToken, refreshToken)

        // should
        accessTokenRepository.accessToken shouldBe accessToken
        accessTokenRepository.refreshToken shouldBe refreshToken
    }

    @Test
    fun test() {
        val accessToken = randomToken()
        accessTokenRepository.putToken(accessToken, "")

        // given
        mockWebServer.enqueueResponse("current-playback.json")
        runBlocking { webService.getCurrentPlayback() }

        // should
        val recordedRequest = mockWebServer.takeRequest()
        recordedRequest.headers["Authorization"] shouldBe "Bearer $accessToken"
    }

    @Test
    fun test2() = runBlocking {
        val accessToken = randomToken()
        val refreshToken = randomToken()
        val newAccessToken = randomToken()
        val newRefreshToken = randomToken()

        accessTokenRepository.putToken(accessToken, refreshToken)

        coEvery { authService.refreshToken(refreshToken, any(), any()) } returns
            CodeExchangeResponse(newAccessToken, "Bearer", "", 60, newRefreshToken)

        // given
        mockWebServer.enqueueResponse("current-playback.json")
        webService.getCurrentPlayback()

        mockWebServer.enqueueResponse("error-authentication.json", 401)
        mockWebServer.enqueueResponse("current-playback.json")
        webService.getCurrentPlayback()

        // should
        accessTokenRepository.accessToken shouldBe newAccessToken
        accessTokenRepository.refreshToken shouldBe newRefreshToken
    }
}