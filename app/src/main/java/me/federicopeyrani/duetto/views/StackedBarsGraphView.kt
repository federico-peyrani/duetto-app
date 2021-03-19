package me.federicopeyrani.duetto.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.withSave
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

        var barWidth = 0f
            private set

        /**
         * Draws this bar on the given canvas, accounting for the space occupied by the bars
         * drawn before this one, indicated by the parameter [position].
         */
        fun draw(canvas: Canvas, position: Float) {
            barWidth = value * width
            rect.set(position, 0f, position + barWidth, barHeight)
            canvas.drawRect(rect, paint)
        }
    }

    // endregion

    companion object {
        private const val DEFAULT_BARS_NUM = 6

        private const val DEFAULT_BARS_COLOR = Color.BLACK

        private const val DEFAULT_BARS_ALPHA = 56

        private const val DEFAULT_CORNER_RADIUS = 24f

        private const val DEFAULT_CIRCLE_RADIUS = 24f

        private const val SETTLE_ANIMATION_DURATION = 400L

        private const val LABEL_INITIAL_SPACING = 0f

        /** Margins around the label's circle. */
        private const val LABEL_CIRCLE_SPACING = 10f

        /** Extra spacing after each label (not counting [LABEL_CIRCLE_SPACING]). */
        private const val LABEL_TEXT_SPACING = 20f
    }

    private val barClipPath = Path()

    private val barClipRect = RectF()

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

    private val cornerRadius: Float

    private val circleRadius: Float

    private val circleY get() = height - circleRadius

    private val textY get() = circleY - (labelTextPain.descent() + labelTextPain.ascent()) / 2

    private val barHeight get() = circleY - circleRadius - LABEL_CIRCLE_SPACING

    private val labelTextPain: Paint

    private val textAllCaps: Boolean

    // endregion

    init {
        val theme = context.theme

        context.obtainStyledAttributes(attributeSet, R.styleable.StackedBarsGraphView).apply {
            // get corner radius dimension or use default DEFAULT_CORNER_RADIUS value
            cornerRadius = getDimension(
                R.styleable.StackedBarsGraphView_graphCornerRadius,
                DEFAULT_CORNER_RADIUS)

            circleRadius = getDimension(
                R.styleable.StackedBarsGraphView_labelCircleSize,
                DEFAULT_CIRCLE_RADIUS)

            val ap = getResourceId(R.styleable.StackedBarsGraphView_labelTextAppearance, -1)
            val appearance = if (ap != -1) {
                theme.obtainStyledAttributes(ap, R.styleable.TextAppearance)
            } else {
                val value = TypedValue()
                theme.resolveAttribute(R.attr.textAppearanceSubtitle2, value, false)
                theme.obtainStyledAttributes(value.data, R.styleable.TextAppearance)
            }

            appearance.apply {
                labelTextPain = getTextPaint()

                // extra text attributes
                textAllCaps = getBoolean(R.styleable.TextAppearance_textAllCaps, false)

                recycle()
            }

            recycle()
        }
    }

    private fun TypedArray.getTextPaint() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = getDimension(R.styleable.TextAppearance_android_textSize, 0f)

        val fontFamily = getString(R.styleable.TextAppearance_fontFamily)
        val fontStyle = getInt(R.styleable.TextAppearance_android_textStyle, Typeface.NORMAL)
        typeface = Typeface.create(fontFamily, fontStyle)

        color = getColor(R.styleable.TextAppearance_android_textColor, Color.BLACK)
    }

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        barClipRect.set(0f, 0f, w.toFloat(), barHeight)
        barClipPath.apply {
            reset()
            addRoundRect(barClipRect, cornerRadius, cornerRadius, Path.Direction.CW)
            close()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        canvas.withSave {
            canvas.clipPath(barClipPath)

            drawBars.fold(0f) { position, drawBar ->
                drawBar.draw(canvas, position)
                position + drawBar.barWidth
            }
        }

        bars?.zip(drawBars)?.fold(LABEL_INITIAL_SPACING) { position, (bar, drawBar) ->
            var cursor = position

            cursor += circleRadius
            canvas.drawCircle(cursor, circleY, circleRadius, drawBar.paint)

            cursor += circleRadius + LABEL_CIRCLE_SPACING
            val text = if (textAllCaps) bar.label.toUpperCase() else bar.label
            canvas.drawText(text, cursor, textY, labelTextPain)

            cursor += labelTextPain.measureText(text) + LABEL_TEXT_SPACING
            cursor
        }
    }
}
