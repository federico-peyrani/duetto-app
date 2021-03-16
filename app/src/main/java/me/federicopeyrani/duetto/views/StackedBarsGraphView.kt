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

    private class PaintList(
        preallocateSize: Int,
        private val builder: Paint.() -> Unit
    ) {

        private val mutableList = mutableListOf<Paint>()

        init {
            repeat(preallocateSize) {
                val newPaint = Paint()
                newPaint.builder()
                mutableList.add(newPaint)
            }
        }

        operator fun get(index: Int): Paint = if (index > mutableList.size - 1) {
            val newPaint = Paint()
            newPaint.builder()
            mutableList.add(newPaint)
            newPaint
        } else {
            mutableList[index]
        }
    }

    companion object {
        private const val PREALLOCATE_PAINT_SIZE = 8
        private const val DEFAULT_NUM_BARS = 6
        private const val SETTLE_ANIMATION_DURATION = 800L
    }

    private val paints = PaintList(PREALLOCATE_PAINT_SIZE) {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        color = Random.nextInt()
    }

    /**
     * The actual list of values used to draw the bars, the values get update at each call
     * of [settleAnimationListener]
     */
    private val _values: MutableList<Float> = generateSequence { 1f }
        .take(DEFAULT_NUM_BARS)
        .toList()
        .normalize()
        .toMutableList()

    /**
     * The values used to draw the bars of the graph, if a non-null value is set, then a settling
     * animation to display the bars adjusting to the actual new values.
     */
    var values: List<Float>? = null
        set(value) {
            // normalize the values once they are set, to save on computation time during onDraw()
            field = value?.normalize()?.apply {
                // if the size of the new values exceeds the number of bars currently in the graph,
                // initialize those bars to 0, so that they will expand to match the actual new
                // value.
                repeat(size - _values.size) { _values.add(0f) }
                startSettleAnimation()
            }
        }

    var colors: List<Int>? = null
        set(value) {
            field = value
            value?.forEachIndexed { index, i -> paints[index].color = i }
        }

    private fun Iterable<Float>.normalize(): List<Float> {
        val sum = sum()
        return map { it / sum }
    }

    private fun settleAnimationListener(animatedFraction: Float) {
        values?.forEachIndexed { index, endValue ->
            _values[index] = _values[index] * (1 - animatedFraction) + endValue * animatedFraction
        }
        invalidate()
    }

    private fun startSettleAnimation() = ValueAnimator.ofFloat(0f, 100f).apply {
        duration = SETTLE_ANIMATION_DURATION
        interpolator = DecelerateInterpolator()
        addUpdateListener { settleAnimationListener(animatedFraction) }
        start()
    }

    private fun Canvas.addBar(index: Int, position: Float, size: Float): Float {
        drawRect(position, 0f, position + size, measuredHeight.toFloat(), paints[index])
        return position + size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        _values.map { it * measuredWidth }
            .foldIndexed(0f) { index, position, size -> canvas.addBar(index, position, size) }
    }
}