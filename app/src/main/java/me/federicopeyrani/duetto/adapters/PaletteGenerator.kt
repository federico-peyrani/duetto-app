package me.federicopeyrani.duetto.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaletteGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) {

    suspend fun loadBitmap(path: String): Bitmap = withContext(Dispatchers.IO) {
        val imageRequest = ImageRequest.Builder(context)
            .allowHardware(false)
            .data(path)
            .build()
        val result = (imageLoader.execute(imageRequest) as SuccessResult).drawable
        (result as BitmapDrawable).bitmap
    }

    suspend inline fun generatePalette(bitmap: Bitmap): Palette = withContext(Dispatchers.Default) {
        Palette.from(bitmap).generate()
    }
}