package me.federicopeyrani.duetto.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.random.Random

class StackedBarsGraphView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    class Bar(
        var value: Float,
        var color: Int = Random.nextInt(),
    ) {

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.FILL
            color = this@Bar.color
        }
    }

    companion object {
        private const val DEFAULT_NUM_BARS = 6
        private const val SETTLE_ANIMATION_DURATION = 400L
    }

    /**
     * The actual list of values used to draw the bars, the values get update at each call
     * of [ValueAnimator.addUpdateListener].
     */
    private val _bars = generateSequence { Bar(1f) }
        .take(DEFAULT_NUM_BARS)
        .toList()
        .normalize()
        .toMutableList()

    /**
     * The values used to draw the bars of the graph, if a non-null value is set, then a settling
     * animation to display the bars adjusting to the actual new values.
     */
    var bars: List<Bar>? = null
        set(value) {
            // normalize the values once they are set, to save on computation time during onDraw()
            field = value?.normalize()?.apply {
                // if the size of the new values exceeds the number of bars currently in the graph,
                // initialize those bars to 0, so that they will expand to match the actual new
                // value.
                takeLast(size - _bars.size).forEach { _bars.add(Bar(0f, it.color)) }
                val endValues = map { it.value }
                startSettleAnimation(endValues)
            }
        }

    private fun List<Bar>.normalize(): List<Bar> = apply {
        val sum = map { it.value }.sum()
        forEach { it.value /= sum }
    }

    private fun ValueAnimator.interpolate(startValue: Float, endValue: Float) =
        startValue * (1 - animatedFraction) + endValue * animatedFraction

    private fun startSettleAnimation(endValues: List<Float>) = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = SETTLE_ANIMATION_DURATION
        interpolator = DecelerateInterpolator()

        val startValues = _bars.map { it.value }
        addUpdateListener {
            _bars.forEachIndexed { index, bar ->
                bar.value = interpolate(startValues[index], endValues[index])
            }
            invalidate()
        }

        start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        _bars.fold(0f) { position, bar ->
            val width = bar.value * measuredWidth
            canvas.drawRect(position, 0f, position + width, measuredHeight.toFloat(), bar.paint)
            position + width
        }
    }
}