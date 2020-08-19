package dev.brella.kornea.toolkit.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import dev.brella.kornea.toolkit.common.SharedState

public class SharedStateMutex<T>(private var state: T, private val mutex: Mutex): SharedState<T, T> {
    public constructor(state: T): this(state, Mutex())

    override suspend fun beginRead(): T {
        mutex.lock()
        return state
    }
    override suspend fun beginWrite(): T {
        mutex.lock()
        return state
    }

    override suspend fun finishRead() {
        mutex.unlock()
    }

    override suspend fun finishWrite(state: T) {
        this.state = state
        mutex.unlock()
    }

    override suspend fun read(): T = mutex.withLock { state }
}