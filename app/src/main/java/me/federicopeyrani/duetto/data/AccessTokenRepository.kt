package me.federicopeyrani.duetto.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val SHARED_PREFS_NAME = "login"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

    val accessToken: String? get() = sharedPrefs.getString(KEY_ACCESS_TOKEN, null)

    val refreshToken: String? get() = sharedPrefs.getString(KEY_REFRESH_TOKEN, null)

    fun putToken(accessToken: String, refreshToken: String) {
        sharedPrefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }
}