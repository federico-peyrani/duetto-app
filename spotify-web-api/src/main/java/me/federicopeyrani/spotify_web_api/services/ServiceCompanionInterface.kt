package me.federicopeyrani.spotify_web_api.services

interface ServiceCompanionInterface<T> {

    val baseUrl: String

    val clazz: Class<T>
}