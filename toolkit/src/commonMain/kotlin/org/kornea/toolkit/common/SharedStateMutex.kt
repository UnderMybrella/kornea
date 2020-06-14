package org.kornea.toolkit.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.abimon.kornea.annotations.ExperimentalKorneaToolkit

public data class SharedStateMutex<T>(private var state: T, private val mutex: Mutex) {
    public constructor(state: T): this(state, Mutex())

    public suspend fun <R> accessState(block: suspend (T) -> R): R =
        mutex.withLock { block(state) }

    public suspend fun mutateState(block: suspend (T) -> T): SharedStateMutex<T> {
        mutex.withLock { block(state) }

        return this
    }
}