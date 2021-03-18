package me.federicopeyrani.duetto.utils

/**
 * Returns the output of [takeLast] if [n] is positive, otherwise it returns [emptyList] without
 * throwing an exception.
 */
fun <T> List<T>.takeLastIf(n: Int): List<T> = if (n > 0) takeLast(n) else emptyList()