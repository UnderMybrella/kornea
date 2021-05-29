package dev.brella.kornea.errors.common

import dev.brella.kornea.errors.common.atomicfu.successPooledWithLatch

public actual fun <T> KorneaResult.Companion.successPooled(value: T): KorneaResult<T> =
    successPooledWithLatch(value)