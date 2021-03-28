package me.federicopeyrani.duetto.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend inline fun Bitmap.generatePalette(): Palette? = suspendCoroutine { cont ->
    Palette.from(this).generate { cont.resume(it) }
}

fun Continuation<Bitmap>.asTarget(): Target = object : Target {

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
        resume(bitmap)
    }

    override fun onBitmapFailed(e: java.lang.Exception, errorDrawable: Drawable?) {
        resumeWithException(e)
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
    }
}

suspend inline fun loadBitmap(path: String): Bitmap = suspendCoroutine { cont ->
    Picasso.get().load(path).into(cont.asTarget())
}
