package me.federicopeyrani.duetto.util

import kotlinx.coroutines.runBlocking

internal fun test(block: suspend () -> Unit) = runBlocking { block() }