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
import me.federicopeyrani.duetto.databinding.ActivityFirstLaunchBinding

class FirstLaunchActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 1337
        private const val CLIENT_ID = "f11ce41c55004d0b95de68fa5d018a25"
        private const val REDIRECT_URI = "deify-login://callback"

        private const val SHARED_PREFS_NAME = "login"
        private const val TOKEN_KEY = "token"

        private val SCOPES = arrayOf("user-top-read")

        const val AUTH_OUTCOME = "me.federicopeyrani.duetto.AUTH_OUTCOME"
        const val AUTH_OUTCOME_SUCCESS = 1
    }

    /** The view binding for this activity. */
    private lateinit var binding: ActivityFirstLaunchBinding

    private lateinit var prefs: SharedPreferences

    private fun handleResponse(response: AuthorizationResponse) {
        when (response.type) {

            Type.TOKEN -> {
                // save token
                prefs.edit { putString(TOKEN_KEY, response.accessToken) }

                // return to main activity
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(AUTH_OUTCOME, AUTH_OUTCOME_SUCCESS)
                }
                startActivity(intent)
            }

            Type.ERROR -> {
                // display error message
                val snackbar = Snackbar.make(binding.root,
                                             response.error,
                                             Snackbar.LENGTH_LONG)
                snackbar.show()
            }

            else -> Log.d("AUTH", "Auth other")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstLaunchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get SharedPreferences instance
        prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

        // set ClickListener for login button
        binding.loginButton.setOnClickListener {
            val builder = AuthorizationRequest.Builder(CLIENT_ID, Type.TOKEN, REDIRECT_URI)
            builder.setScopes(SCOPES)

            // build the request and then start the login activity, which is part of the Spotify SDK
            val request = builder.build()
            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when (requestCode) {
            REQUEST_CODE -> {
                val response = AuthorizationClient.getResponse(resultCode, intent)
                handleResponse(response)
            }
        }
    }
}