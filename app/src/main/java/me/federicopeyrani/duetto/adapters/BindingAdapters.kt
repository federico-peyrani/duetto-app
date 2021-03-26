package me.federicopeyrani.duetto.adapters

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom.DISK
import com.squareup.picasso.Picasso.LoadedFrom.NETWORK
import com.squareup.picasso.Target
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.evaluators.AlphaSatColorMatrixEvaluator
import me.federicopeyrani.spotify_web_api.objects.ImageObject

private class LoadImageTarget(private val view: ImageView) : Target {

    private fun startColorFilterAnimation() {
        val evaluator = AlphaSatColorMatrixEvaluator()
        val colorMatrix = evaluator.colorMatrix

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 800
            addUpdateListener {
                evaluator.evaluate(it.animatedValue as Float, null, null)
                val filter = ColorMatrixColorFilter(colorMatrix)
                view.colorFilter = filter
                view.invalidate()
            }
            start()
        }
    }

    override fun onBitmapLoaded(bitmap: Bitmap, loadedFrom: Picasso.LoadedFrom?) {
        view.setImageBitmap(bitmap)

        if (loadedFrom == NETWORK || loadedFrom == DISK) {
            startColorFilterAnimation()
        }
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        Log.e("loadImage", e?.cause?.message ?: "null")
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable) {
        view.setImageDrawable(placeHolderDrawable)
    }
}

@BindingAdapter("albumArtUrls")
fun loadAlbumArtFromUrls(view: ImageView, albumArtUrls: Array<ImageObject>?) {
    // Choose the smallest image that is at least bigger or equal than the size of the
    // ImageView, sorting them by increasing size (even though they are already sorted by
    // decreasing height, sorting them makes the code more readable).
    val height = view.height
    val url = albumArtUrls?.sortedBy { it.height }?.first { it.height >= height }?.url

    Picasso.get()
        .load(url)
        .placeholder(R.drawable.img_album_art_placeholder)
        .into(LoadImageTarget(view))
}

@BindingAdapter("albumArtBitmap")
fun loadAlbumArtFromBitmap(view: ImageView, bitmap: Bitmap?) {
    val layoutParams = view.layoutParams
    layoutParams.height = view.width
    view.layoutParams = layoutParams
    view.setImageBitmap(bitmap)
}