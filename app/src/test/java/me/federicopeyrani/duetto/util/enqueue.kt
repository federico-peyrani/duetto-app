package me.federicopeyrani.duetto.util

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets

internal fun MockWebServer.enqueueResponse(fileName: String, code: Int = 200) {
    val inputStream = javaClass.classLoader?.getResourceAsStream("objects/$fileName")

    val source = inputStream?.let { inputStream.source().buffer() }
    source?.let {
        val response = MockResponse()
            .setResponseCode(code)
            .setBody(it.readString(StandardCharsets.UTF_8))
        enqueue(response)
    }
}

internal fun MockWebServer.enqueueEmptyResponse(code: Int = 204) {
    val response = MockResponse().apply {
        setResponseCode(code)
    }
    enqueue(response)
}