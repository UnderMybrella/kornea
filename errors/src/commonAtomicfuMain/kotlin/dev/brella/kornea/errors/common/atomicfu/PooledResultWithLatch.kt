package dev.brella.kornea.errors.common.atomicfu

import dev.brella.kornea.base.common.*
import dev.brella.kornea.errors.common.KorneaResult
import kotlinx.atomicfu.atomic

private class PooledResultWithLatch<T>(
    private var value: Any?,
    private var returnTo: RingBuffer<PooledResultWithLatch<Any?>>
) : KorneaResult.Success<T> {
    private object IDLE

    private var latch = atomic(1)

    companion object {
        private inline fun <R> cast(result: PooledResultWithLatch<*>): PooledResultWithLatch<R> =
            result as PooledResultWithLatch<R>

        internal val defaultPool: RingBuffer<PooledResultWithLatch<Any?>> = ListRingBuffer.withCapacity(1_00)
    }

    override fun get(): T =
        if (value === IDLE) throw IllegalStateException("PooledResultWithLatch<T> was closed") else value as T

    override fun <R> mapValue(newValue: R): KorneaResult<R> =
        if (latch.value <= 1) {
            this.value = newValue
            cast(this)
        } else {
            latch.value -= 1
            PooledResultWithLatch(newValue, returnTo)
        }

    override fun dataHashCode(): Optional<Int> =
        if (value === IDLE) Optional.empty() else Optional.of(value.hashCode())

    override fun isAvailable(dataHashCode: Int?): Boolean? =
        if (value === IDLE) false else if (dataHashCode?.equals(value.hashCode()) != false) true else null

    override fun consume(dataHashCode: Int?) {
        if (value !== IDLE) {
            if (dataHashCode == null || dataHashCode == value.hashCode()) {
                latch -= 1
                if (latch.value == 0) {
                    value = IDLE
                    returnTo.push(cast(this))
                }
            }
        }
    }

    override fun copyOf(): KorneaResult<T> {
        latch += 1

        return this
    }

    override fun toString(): String =
        "PooledResultWithLatch(value=$value,returnTo=$returnTo,latch=$latch)"
}

public fun <T> KorneaResult.Companion.successPooledWithLatch(value: T): KorneaResult<T> =
    PooledResultWithLatch.defaultPool
        .pop()
        .map { resting -> resting.mapValue(value) }
        .getOrElseRun { PooledResultWithLatch(value, PooledResultWithLatch.defaultPool) }