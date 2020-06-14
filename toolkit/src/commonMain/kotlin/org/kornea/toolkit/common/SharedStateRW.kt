package org.kornea.toolkit.common

import org.abimon.kornea.annotations.ExperimentalKorneaToolkit

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
public data class SharedStateRW<T>(private var state: T, private val semaphore: ReadWriteSemaphore) {
    public constructor(state: T, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public suspend fun <R> accessState(block: suspend (T) -> R): R =
        semaphore.withReadPermit { block(state) }

    public suspend fun mutateState(block: suspend (T) -> T): SharedStateRW<T> {
        semaphore.withWritePermit { state = block(state) }

        return this
    }
}