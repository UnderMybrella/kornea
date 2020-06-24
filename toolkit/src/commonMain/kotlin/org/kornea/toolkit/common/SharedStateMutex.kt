package org.kornea.toolkit.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.abimon.kornea.annotations.ExperimentalKorneaToolkit

public class SharedStateMutex<T>(private var state: T, private val mutex: Mutex): SharedState<T, T> {
    public constructor(state: T): this(state, Mutex())

    public override suspend fun <R> accessState(block: suspend (T) -> R): R =
        mutex.withLock { block(state) }

    public override suspend fun mutateState(block: suspend (T) -> T): SharedStateMutex<T> {
        mutex.withLock { block(state) }

        return this
    }
}