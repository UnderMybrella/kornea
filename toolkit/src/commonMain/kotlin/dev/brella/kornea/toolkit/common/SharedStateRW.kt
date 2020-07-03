package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
public class SharedStateRW<T>(private var state: T, private val semaphore: ReadWriteSemaphore): SharedState<T, T> {
    public constructor(state: T, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    override suspend fun beginRead(): T {
        semaphore.acquireReadPermit()
        return state
    }
    override suspend fun beginWrite(): T {
        semaphore.acquireWritePermit()
        return state
    }

    override suspend fun finishRead() {
        semaphore.releaseReadPermit()
    }

    override suspend fun finishWrite(state: T) {
        this.state = state
        semaphore.releaseWritePermit()
    }

    override suspend fun read(): T = semaphore.withReadPermit { state }
}