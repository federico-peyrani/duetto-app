package me.federicopeyrani.duetto.data

import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.StateFlow

class Track(
    val title: StateFlow<String>,
    val artist: StateFlow<String>,
    val albumArt: StateFlow<Drawable>,
)