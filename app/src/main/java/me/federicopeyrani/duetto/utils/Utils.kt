package me.federicopeyrani.duetto.utils

import android.util.Base64
import java.security.MessageDigest

object Utils {

    private val allowedChars = ('A'..'Z') + ('a'..'z')

    fun randomString(length: Int) = (1..length).map { allowedChars.random() }.joinToString("")

    fun String.sha256(): ByteArray {
        val bytes = toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes)
    }

    fun ByteArray.toBase64Url(): String =
        Base64.encodeToString(this, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}