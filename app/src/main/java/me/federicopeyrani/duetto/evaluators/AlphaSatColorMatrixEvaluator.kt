package me.federicopeyrani.duetto.evaluators

import android.animation.TypeEvaluator
import android.graphics.ColorMatrix
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class AlphaSatColorMatrixEvaluator : TypeEvaluator<ColorMatrix> {

    private val elements = FloatArray(20)

    val colorMatrix: ColorMatrix = ColorMatrix()

    override fun evaluate(
        fraction: Float,
        startValue: ColorMatrix?,
        endValue: ColorMatrix?,
    ): ColorMatrix {
        // There are 3 phases so we multiply fraction by that amount
        val phase = fraction * 3

        // Compute the alpha change over period [0, 2]
        val alpha = min(phase, 2f) / 2f
        // elements [19] = (float) Math.round(alpha * 255);
        elements[18] = alpha

        // We subtract to make the picture look darker, it will automatically clamp
        // This is spread over period [0, 2.5]
        val maxBlacker = 100
        val blackening = ((1 - phase.coerceAtMost(2.5f) / 2.5f) * maxBlacker).roundToInt().toFloat()
        elements[14] = -blackening
        elements[9] = -blackening
        elements[4] = -blackening

        // Finally we desaturate over [0, 3], taken from ColorMatrix.SetSaturation
        val invSat = 1 - max(0.2f, fraction)
        val r = 0.213f * invSat
        val g = 0.715f * invSat
        val b = 0.072f * invSat
        elements[0] = r + fraction
        elements[1] = g
        elements[2] = b
        elements[5] = r
        elements[6] = g + fraction
        elements[7] = b
        elements[10] = r
        elements[11] = g
        elements[12] = b + fraction

        colorMatrix.set(elements)
        return colorMatrix
    }
}