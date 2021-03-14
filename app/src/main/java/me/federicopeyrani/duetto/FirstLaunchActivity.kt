package me.federicopeyrani.duetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.AuthorizationResponse.Type
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.federicopeyrani.duetto.consts.ClientParams.CLIENT_ID
import me.federicopeyrani.duetto.consts.ClientParams.REDIRECT_URI
import me.federicopeyrani.duetto.consts.ClientParams.SCOPES
import me.federicopeyrani.duetto.data.AccessTokenRepository
import me.federicopeyrani.duetto.databinding.ActivityFirstLaunchBinding
import me.federicopeyrani.duetto.utils.Utils.randomString
import me.federicopeyrani.duetto.utils.Utils.sha256
import me.federicopeyrani.duetto.utils.Utils.toBase64Url
import me.federicopeyrani.spotify_web_api.services.AuthService
import javax.inject.Inject

@AndroidEntryPoint
class FirstLaunchActivity : AppCompatActivity() {

    companion object {
        private const val CODE_VERIFIER_LENGTH = 128
        private const val STATE_LENGTH = 8

        const val AUTH_OUTCOME = "me.federicopeyrani.duetto.AUTH_OUTCOME"
        const val AUTH_OUTCOME_SUCCESS = 1
    }

    /** The view binding for this activity. */
    private lateinit var binding: ActivityFirstLaunchBinding

    private lateinit var codeVerifier: String
    private lateinit var state: String

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var accessTokenRepository: AccessTokenRepository

    private fun onLoginButtonClicked() {
        // generate random strings for the state and code verifier
        codeVerifier = randomString(CODE_VERIFIER_LENGTH)
        state = randomString(STATE_LENGTH)
        val codeChallenge = codeVerifier.sha256().toBase64Url()

        // build the request
        val request = AuthorizationRequest.Builder(CLIENT_ID, Type.CODE, REDIRECT_URI).apply {
            // add additional parameters to the request
            setScopes(SCOPES)
            setState(state)
            setCustomParam("code_challenge_method", "S256")
            setCustomParam("code_challenge", codeChallenge)
        }.build()

        AuthorizationClient.openLoginInBrowser(this, request)
    }

    private fun onCode(response: AuthorizationResponse) = CoroutineScope(Dispatchers.IO).launch {
        val codeExchangeResponse = authService.getToken(
            clientId = CLIENT_ID,
            code = response.code,
            redirectUri = REDIRECT_URI,
            codeVerifier = codeVerifier
        )

        // save refresh token
        Log.d("Login", "Saving token")
        accessTokenRepository.putToken(
            accessToken = codeExchangeResponse.accessToken,
            refreshToken = codeExchangeResponse.refreshToken
        )

        withContext(Dispatchers.Main) {
            // return to main activity
            Intent(this@FirstLaunchActivity, MainActivity::class.java).apply {
                putExtra(AUTH_OUTCOME, AUTH_OUTCOME_SUCCESS)
                startActivity(this)
            }
        }
    }

    private fun onError(response: AuthorizationResponse) {
        // display error message
        val snackbar = Snackbar.make(binding.root, response.error, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstLaunchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // set ClickListener for login button
        binding.loginButton.setOnClickListener { onLoginButtonClicked() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.let {
            val response = AuthorizationResponse.fromUri(it)
            when (response.type) {
                Type.CODE -> onCode(response)
                Type.ERROR -> onError(response)
                else -> Log.d("AUTH", "Auth other")
            }
        }
    }
}