package me.federicopeyrani.duetto.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import me.federicopeyrani.spotify_web_api.objects.ImageObject

@BindingAdapter("albumArtUrls")
fun loadImage(view: ImageView, albumArtUrls: Array<ImageObject>?) {
    // Choose the smallest image that is at least bigger or equal than the size of the
    // ImageView, sorting them by increasing size (even though they are already sorted by
    // decreasing height, sorting them makes the code more readable).
    val height = view.height
    val url = albumArtUrls?.sortedBy { it.height }?.first { it.height >= height }?.url

    Picasso.get().load(url).into(view)
}