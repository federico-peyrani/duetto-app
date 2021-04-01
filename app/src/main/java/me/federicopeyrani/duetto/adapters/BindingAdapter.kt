package me.federicopeyrani.duetto.adapters

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.android.material.chip.Chip
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

    private inline fun request(builder: ImageRequest.Builder.() -> Unit) {
        val request = ImageRequest.Builder(context).apply(builder).build()
        imageLoader.enqueue(request)
    }

    @BindingAdapter("albumArtUrls")
    fun ImageView.loadImage(albumArtUrls: Array<ImageObject>?) {
        // Choose the smallest image that is at least bigger or equal than the size of the
        // ImageView, sorting them by increasing size (even though they are already sorted by
        // decreasing height, sorting them makes the code more readable).
        val url = albumArtUrls?.sortedBy { it.height }?.first { it.height >= height }?.url ?: return

        request {
            data(url)
            target(this@loadImage)
        }
    }

    @BindingAdapter("artists")
    fun TextView.displayArtists(artists: Array<Artist>?) {
        text = artists?.joinToString(", ") { it.name }
    }

    @BindingAdapter("artistImages")
    fun ImageView.loadArtistsImage(images: Array<ImageObject>?) {
        val width = width
        val url = images?.sortedBy { it.width }?.first { it.width >= width }?.url ?: return

        val layoutParams = layoutParams
        layoutParams.height = this.width
        this.layoutParams = layoutParams

        request {
            data(url)
            target(this@loadArtistsImage)
        }
    }

    @BindingAdapter("chipArtistImage")
    fun Chip.loadChipArtistImage(images: Array<ImageObject>?) {
        val url = images?.minByOrNull { it.width }?.url ?: return
        request {
            data(url)
            target(onSuccess = { chipIcon = it })
            transformations(RoundedCornersTransformation(chipCornerRadius))
        }
    }

    @BindingAdapter("date")
    fun TextView.displayDate(date: Date?) {
        date?.let {
            val dateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            val timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(it)
            text = this@BindingAdapter.context.getString(
                R.string.play_history_date_time_format,
                dateString,
                timeString
            )
        }
    }

    @BindingAdapter("albumArtBitmap")
    fun ImageView.loadAlbumArtFromBitmap(bitmap: Bitmap?) {
        val mLayoutParams = layoutParams
        mLayoutParams.height = width
        layoutParams = mLayoutParams
        setImageBitmap(bitmap)
    }
}