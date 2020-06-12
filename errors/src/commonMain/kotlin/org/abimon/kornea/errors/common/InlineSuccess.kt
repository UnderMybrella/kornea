package org.abimon.kornea.errors.common

import org.abimon.kornea.annotations.ExperimentalKorneaErrors

@Suppress("UNCHECKED_CAST")
private inline class InlineSuccess<out T>(private val value: Any?) : KorneaResult.Success<T> {
    override fun get(): T = value as T
}

@ExperimentalKorneaErrors
public fun <T> KorneaResult.Companion.successInline(value: T): KorneaResult<T> = InlineSuccess(value)