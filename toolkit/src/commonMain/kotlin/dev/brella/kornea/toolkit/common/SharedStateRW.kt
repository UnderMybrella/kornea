package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit

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