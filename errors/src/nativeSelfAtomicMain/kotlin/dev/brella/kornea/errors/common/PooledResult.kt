package dev.brella.kornea.errors.common

import dev.brella.kornea.base.common.getOrElseRun
import dev.brella.kornea.base.common.map

public actual fun <T> KorneaResult.Companion.successPooled(value: T): KorneaResult<T> =
    PooledResult.defaultPool
        .pop()
        .map { resting -> resting.mapValue(value) }
        .getOrElseRun { PooledResult(value, PooledResult.defaultPool) }