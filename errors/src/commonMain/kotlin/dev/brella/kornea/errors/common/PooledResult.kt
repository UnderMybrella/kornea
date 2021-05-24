package dev.brella.kornea.errors.common

import dev.brella.kornea.base.common.*

private class PooledResult<T>(private var value: Any?, private var returnTo: RingBuffer<PooledResult<Any?>>) : KorneaResult.Success<T> {
    private object IDLE

    companion object {
        private inline fun <R> cast(result: PooledResult<*>): PooledResult<R> = result as PooledResult<R>

        internal val defaultPool: RingBuffer<PooledResult<Any?>> = ListRingBuffer.withCapacity(1_00)
    }

    override fun get(): T = if (value === IDLE) throw IllegalStateException("PooledResult<T> was closed") else value as T
    override fun <R> mapValue(newValue: R): KorneaResult<R> {
        this.value = newValue
        return cast(this)
    }

    override fun dataHashCode(): Optional<Int> =
        if (value === IDLE) Optional.empty() else Optional.of(value.hashCode())

    override fun isAvailable(dataHashCode: Int?): Boolean? =
        if (value === IDLE) false else if (dataHashCode?.equals(value.hashCode()) != false) true else null

    override fun consume(dataHashCode: Int?) {
        if (value !== IDLE) {
            if (dataHashCode == null || dataHashCode == value.hashCode()) {
                value = IDLE
                returnTo.push(cast(this))
            }
        }
    }
}

public fun <T> KorneaResult.Companion.successPooled(value: T): KorneaResult<T> =
    PooledResult.defaultPool
        .pop()
        .map { resting -> resting.mapValue(value) }
        .getOrElseRun { PooledResult(value, PooledResult.defaultPool) }