package me.federicopeyrani.duetto.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.federicopeyrani.duetto.consts.ClientParams
import me.federicopeyrani.spotify_web_api.services.AuthService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenAuthenticator @Inject constructor(
    private val accessTokenRepository: AccessTokenRepository,
    private val authService: AuthService,
) : Authenticator {

    /** Coroutine context ot be used when performing the call to refresh the token. */
    private val context = Dispatchers.IO

    private val Response.isRequestWithAccessToken: Boolean
        get() {
            val header = request.header("Authorization")
            return header != null && header.startsWith("Bearer")
        }

    private fun Response.setAuthorizationHeader(accessToken: String) = request.newBuilder().apply {
        header("Authorization", "Bearer $accessToken")
    }.build()

    /**
     * Fetches synchronously the most recent access token, or attempts to refresh it using the
     * refresh.
     *
     * @param accessToken the access token that has been fetched before calling the method,
     * comparing it to the one currently in [AccessTokenRepository] will avoid refreshing the token
     * by multiple threads if not needed.
     *
     * @return a valid access token to be used for future calls, either by refreshing it or by
     * fetching the most recent one if another thread as already refreshed it.
     */
    @Synchronized
    private fun getOrRefreshToken(accessToken: String): String {
        val synchronizedAccessToken = accessTokenRepository.accessToken!!
        if (synchronizedAccessToken != accessToken) {
            return synchronizedAccessToken
        }

        // refresh token
        Log.d("AccessTokenAuth", "Refreshing token")
        val refreshToken = accessTokenRepository.refreshToken!!
        val codeExchangeResponse = runBlocking(context) {
            authService.refreshToken(
                refreshToken = refreshToken,
                clientId = ClientParams.CLIENT_ID,
            )
        }

        accessTokenRepository.putToken(
            accessToken = codeExchangeResponse.accessToken,
            refreshToken = codeExchangeResponse.refreshToken
        )
        return codeExchangeResponse.accessToken
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = accessTokenRepository.accessToken
        val refreshToken = accessTokenRepository.refreshToken
        if (!response.isRequestWithAccessToken || accessToken == null || refreshToken == null) {
            return null
        }

        // This part of the code is reached only if the original request included an `Authorization`
        // header and we have a valid access token and refresh token; this implies that the requested
        // resource required authentication but said authentication failed.
        // Fetch synchronously the most recent access token, or attempt to refresh it using the
        // refresh token.
        val tokenToUse: String = getOrRefreshToken(accessToken)
        return response.setAuthorizationHeader(tokenToUse)
    }
}