package me.federicopeyrani.duetto.data

import kotlinx.coroutines.flow.StateFlow

class Track(
    val title: StateFlow<String>,
    val artist: StateFlow<String>,
)