package me.federicopeyrani.duetto.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
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
    }

    private val paints = PaintList(PREALLOCATE_PAINT_SIZE) {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        color = Random.nextInt()
    }

    var colors: List<Int>? = null
        set(value) {
            field = value
            value?.forEachIndexed { index, i -> paints[index].color = i }
        }

    var values: List<Float>? = listOf(0.66f, 0.33f / 2, 0.66f)
        set(value) {
            field = value
            invalidate()
        }

    private fun Canvas.addBar(index: Int, position: Float, size: Float): Float {
        drawRect(position, 0f, position + size, measuredHeight.toFloat(), paints[index])
        return position + size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val values = this.values
        if (canvas == null || values == null) return

        val totalSum = values.sum()
        values.map { it / totalSum * measuredWidth }
            .foldIndexed(0f) { index, position, size -> canvas.addBar(index, position, size) }
    }
}