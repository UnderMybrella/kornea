package org.kornea.toolkit.common

import org.abimon.kornea.annotations.ExperimentalKorneaToolkit

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
public class SharedStateRW<T>(private var state: T, private val semaphore: ReadWriteSemaphore): SharedState<T, T> {
    public constructor(state: T, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public override suspend fun <R> accessState(block: suspend (T) -> R): R =
        semaphore.withReadPermit { block(state) }

    public override suspend fun mutateState(block: suspend (T) -> T): SharedStateRW<T> {
        semaphore.withWritePermit { state = block(state) }

        return this
    }
}