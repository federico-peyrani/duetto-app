package me.federicopeyrani.duetto.layoutmanagers

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

class StaticLinearLayoutManager(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayoutManager(context, attrs, defStyleAttr, defStyleRes) {

    override fun canScrollVertically(): Boolean = false
}