package me.federicopeyrani.duetto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.AuthorizationResponse.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.federicopeyrani.duetto.databinding.ActivityFirstLaunchBinding
import me.federicopeyrani.duetto.utils.Utils.randomString
import me.federicopeyrani.duetto.utils.Utils.sha256
import me.federicopeyrani.duetto.utils.Utils.toBase64Url
import me.federicopeyrani.spotify_web_api.services.AuthService

class FirstLaunchActivity : AppCompatActivity() {

    companion object {
        private const val CLIENT_ID = "f11ce41c55004d0b95de68fa5d018a25"
        private const val REDIRECT_URI = "deify-login://callback"

        private const val CODE_VERIFIER_LENGTH = 128
        private const val STATE_LENGTH = 8

        private val SCOPES = arrayOf("user-top-read", "user-read-playback-state")

        const val SHARED_PREFS_NAME = "login"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCESS_TOKEN = "access_token"

        const val AUTH_OUTCOME = "me.federicopeyrani.duetto.AUTH_OUTCOME"
        const val AUTH_OUTCOME_SUCCESS = 1
    }

    /** The view binding for this activity. */
    private lateinit var binding: ActivityFirstLaunchBinding

    private lateinit var prefs: SharedPreferences

    private lateinit var codeVerifier: String
    private lateinit var state: String

    private fun onLoginButtonClicked() {
        // generate random strings for the state and code verifier
        codeVerifier = randomString(CODE_VERIFIER_LENGTH)
        state = randomString(STATE_LENGTH)
        val codeChallenge = codeVerifier.sha256().toBase64Url()

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
        val authService = AuthService.build()
        val codeExchangeResponse = authService.getToken(
            clientId = CLIENT_ID,
            code = response.code,
            redirectUri = REDIRECT_URI,
            codeVerifier = codeVerifier
        )

        // save refresh token
        prefs.edit {
            putString(KEY_REFRESH_TOKEN, codeExchangeResponse.refreshToken)
            putString(KEY_ACCESS_TOKEN, codeExchangeResponse.accessToken)
        }

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

        // get SharedPreferences instance
        prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

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