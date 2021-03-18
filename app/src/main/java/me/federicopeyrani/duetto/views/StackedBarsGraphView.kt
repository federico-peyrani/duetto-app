package me.federicopeyrani.duetto.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.resources.TextAppearance
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.utils.AnimatedProperty
import me.federicopeyrani.duetto.utils.takeLastIf
import kotlin.math.roundToInt
import kotlin.random.Random

class StackedBarsGraphView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    // region Support classes

    class Bar(
        val label: String,
        val value: Float,
        val color: Int = Random.nextInt(),
    )

    private inner class DrawBar(
        startValue: Float = 1f / DEFAULT_BARS_NUM,
        startColor: Int = DEFAULT_BARS_COLOR,
        startAlpha: Int = DEFAULT_BARS_ALPHA,
    ) {

        var value by AnimatedProperty(startValue, ::interpolate)

        var color by AnimatedProperty(startColor, ::interpolateColor)

        var alpha by AnimatedProperty(startAlpha, ::interpolate)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            get() = field.apply {
                style = Paint.Style.FILL
                color = this@DrawBar.color
                alpha = this@DrawBar.alpha
            }

        val rect = RectF()

        var width = 0f
            private set

        /**
         * Draws this bar on the given canvas, accounting for the space occupied by the bars
         * drawn before this one, indicated by the parameter [position].
         */
        fun draw(canvas: Canvas, position: Float) {
            width = value * measuredWidth
            rect.set(position, 0f, position + width, barHeight)
            canvas.drawRect(rect, paint)
        }
    }

    // endregion

    companion object {
        private const val DEFAULT_BARS_NUM = 6

        private const val DEFAULT_BARS_COLOR = Color.WHITE

        private const val DEFAULT_BARS_ALPHA = 56

        private const val SETTLE_ANIMATION_DURATION = 400L

        private const val LABEL_INITIAL_SPACING = 0f

        /** Margins around the label's circle. */
        private const val LABEL_CIRCLE_SPACING = 10f

        /** Extra spacing after each label (not counting [LABEL_CIRCLE_SPACING]). */
        private const val LABEL_TEXT_SPACING = 20f
    }

    // region Animator support

    private val argbEvaluator = ArgbEvaluator()

    private var mAnimatedValue: Float = 1f

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = SETTLE_ANIMATION_DURATION
        interpolator = DecelerateInterpolator()

        addUpdateListener {
            mAnimatedValue = animatedValue as Float
            invalidate()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun interpolate(startValue: Float, endValue: Float) =
        startValue + mAnimatedValue * (endValue - startValue)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun interpolate(startValue: Int, endValue: Int) =
        startValue + (mAnimatedValue * (endValue - startValue)).roundToInt()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun interpolateColor(startValue: Int, endValue: Int) =
        argbEvaluator.evaluate(mAnimatedValue, startValue, endValue) as Int

    // endregion

    /**
     * The actual list of values used to draw the bars, the values get update at each call
     * of [ValueAnimator.addUpdateListener].
     */
    private val drawBars = (0..DEFAULT_BARS_NUM).map { DrawBar() }.toMutableList()

    /**
     * The values used to draw the bars of the graph, if a non-null value is set, then a settling
     * animation to display the bars adjusting to the actual new values.
     */
    var bars: List<Bar>? = null
        set(value) {
            field = value
            value?.onSetBars()
        }

    // region Styling

    @SuppressLint("RestrictedApi")
    val textAppearance = TextAppearance(context, R.style.TextAppearance_MaterialComponents_Body1)

    private val labelTextPain = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textAppearance.textColor?.defaultColor ?: Color.BLACK
        textSize = textAppearance.textSize
    }

    private val circleRadius = labelTextPain.textSize / 2

    private val circleY get() = measuredHeight - circleRadius

    private val textY get() = circleY - (labelTextPain.descent() + labelTextPain.ascent()) / 2

    private val barHeight get() = circleY - circleRadius - LABEL_CIRCLE_SPACING

    // endregion

    /**
     * Called every time a non-null list of bars is set to the the parameter [bars].
     */
    private fun List<Bar>.onSetBars() {
        // If the size of the new values exceeds the number of bars currently in the graph,
        // initialize those bars to 0, so that they will expand to match the actual new
        // value.
        takeLastIf(size - drawBars.size).forEach { drawBars += DrawBar(0f, it.color) }

        // normalize the values once they are set, to save on computation time during onDraw()
        val sum = map { it.value }.sum()
        zip(drawBars).forEach { (bar, drawBar) ->
            // set target values
            drawBar.value = bar.value / sum
            drawBar.color = bar.color
            drawBar.alpha = 255
        }

        // If the number of bars currently drawn on the screen is more than the ones we need to
        // display, then set the target value for the exceeding bars to 0 so that they will
        // collapse and disappear from the screen.
        drawBars.takeLastIf(drawBars.size - size).forEach {
            it.value = 0f
            it.alpha = 0
        }

        animator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        drawBars.fold(0f) { position, drawBar ->
            drawBar.draw(canvas, position)
            position + drawBar.width
        }

        bars?.zip(drawBars)?.fold(LABEL_INITIAL_SPACING) { position, (bar, drawBar) ->
            var cursor = position

            cursor += circleRadius
            canvas.drawCircle(cursor, circleY, circleRadius, drawBar.paint)

            cursor += circleRadius + LABEL_CIRCLE_SPACING
            canvas.drawText(bar.label, cursor, textY, labelTextPain)

            cursor += labelTextPain.measureText(bar.label) + LABEL_TEXT_SPACING
            cursor
        }
    }
}
