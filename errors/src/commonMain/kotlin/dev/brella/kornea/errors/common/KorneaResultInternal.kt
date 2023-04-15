package dev.brella.kornea.errors.common

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T> KorneaResult<T>.getUnsafe(): T =
    value as T

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T> KorneaResult<*>.getCastUnsafe(): T =
    value as T