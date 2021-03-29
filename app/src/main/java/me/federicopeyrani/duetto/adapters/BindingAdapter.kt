package me.federicopeyrani.duetto.adapters

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.data.Artist
import me.federicopeyrani.spotify_web_api.objects.ImageObject
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BindingAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) {

    @BindingAdapter("albumArtUrls")
    fun loadImage(view: ImageView, albumArtUrls: Array<ImageObject>?) {
        // Choose the smallest image that is at least bigger or equal than the size of the
        // ImageView, sorting them by increasing size (even though they are already sorted by
        // decreasing height, sorting them makes the code more readable).
        val height = view.height
        val url = albumArtUrls?.sortedBy { it.height }?.first { it.height >= height }?.url

        val imageRequest = ImageRequest.Builder(context)
            .allowHardware(false)
            .data(url)
            .target(view)
            .build()
        imageLoader.enqueue(imageRequest)
    }

    @BindingAdapter("artists")
    fun displayArtists(view: TextView, artists: Array<Artist>?) {
        view.text = artists?.joinToString(", ") { it.name }
    }

    @BindingAdapter("artistImages")
    fun loadArtistsImage(view: ImageView, images: Array<ImageObject>?) {
        val width = view.width
        val url = images?.sortedBy { it.width }?.first { it.width >= width }?.url

        val layoutParams = view.layoutParams
        layoutParams.height = view.width
        view.layoutParams = layoutParams

        val imageRequest = ImageRequest.Builder(context)
            .allowHardware(false)
            .data(url)
            .target(view)
            .build()
        imageLoader.enqueue(imageRequest)
    }

    @BindingAdapter("date")
    fun displayDate(view: TextView, date: Date?) {
        date?.let {
            val dateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            val timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(it)
            view.text = context.getString(
                R.string.play_history_date_time_format,
                dateString,
                timeString
            )
        }
    }

    @BindingAdapter("albumArtBitmap")
    fun loadAlbumArtFromBitmap(view: ImageView, bitmap: Bitmap?) {
        val layoutParams = view.layoutParams
        layoutParams.height = view.width
        view.layoutParams = layoutParams
        view.setImageBitmap(bitmap)
    }
}