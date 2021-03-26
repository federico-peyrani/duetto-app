package me.federicopeyrani.duetto.data

import androidx.room.TypeConverter
import me.federicopeyrani.spotify_web_api.objects.ImageObject

class Converters {

    @TypeConverter
    fun fromArrayImageObject(imageObjectArray: Array<ImageObject>?): String? =
        imageObjectArray?.joinToString(";") { "${it.height}:${it.width}:${it.url}" }

    @TypeConverter
    fun fromString(string: String?): Array<ImageObject>? =
        string?.split(";")?.map {
            val split = it.split(":")
            ImageObject(split[0].toInt(), split[1].toInt(), split[2])
        }?.toTypedArray()
}