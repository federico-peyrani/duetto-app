package me.federicopeyrani.duetto.utils

import kotlin.reflect.KProperty

class AnimatedProperty<T>(
    private var startValue: T,
    private val interpolator: (startValue: T, endValue: T) -> T,
) {

    private var endValue: T = startValue

    operator fun getValue(thisRef: Any, property: KProperty<*>): T =
        interpolator(startValue, endValue)

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        startValue = interpolator(startValue, endValue)
        endValue = value
    }
}