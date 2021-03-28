package me.federicopeyrani.duetto.data

import androidx.room.TypeConverter
import me.federicopeyrani.spotify_web_api.objects.ImageObject
import java.util.Date

class Converters {

    companion object {

        private const val FIELD_SEPARATOR = "::"

        private const val ELEMENTS_SEPARATOR = ",,"
    }

    private fun join(vararg args: Any) = args.joinToString(FIELD_SEPARATOR)

    private fun <T> Array<T>.serialize(itemSerializer: (T) -> String) =
        joinToString(ELEMENTS_SEPARATOR) { itemSerializer(it) }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromImageObjectArray(imageObjectArray: Array<ImageObject>?): String? =
        imageObjectArray?.serialize { join(it.height, it.width, it.url) }

    @TypeConverter
    fun stringToImageObjectArray(string: String?): Array<ImageObject> =
        if (string == null || string.isNullOrBlank()) {
            emptyArray()
        } else {
            string.split(",,").map {
                val split = it.split("::")
                ImageObject(split[0].toInt(), split[1].toInt(), split[2])
            }.toTypedArray()
        }

    @TypeConverter
    fun fromArtistArray(imageObjectArray: Array<Artist>?): String? =
        imageObjectArray?.serialize { join(it.id, it.name) }

    @TypeConverter
    fun stringToArtistArray(string: String?): Array<Artist> =
        if (string == null || string.isNullOrBlank()) {
            emptyArray()
        } else {
            string.split(",,").map {
                val split = it.split("::")
                Artist(split[0], split[1])
            }.toTypedArray()
        }
}