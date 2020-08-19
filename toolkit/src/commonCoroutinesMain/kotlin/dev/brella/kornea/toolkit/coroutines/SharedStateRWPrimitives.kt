package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.SharedState

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public class SharedStateRWInt(private var state: Int, private val semaphore: ReadWriteSemaphore):
    SharedState<Int, Int> {
    public constructor(state: Int, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    override suspend fun beginRead(): Int {
        semaphore.acquireReadPermit()
        return state
    }
    override suspend fun beginWrite(): Int {
        semaphore.acquireWritePermit()
        return state
    }

    override suspend fun finishRead() {
        semaphore.releaseReadPermit()
    }

    override suspend fun finishWrite(state: Int) {
        this.state = state
        semaphore.releaseWritePermit()
    }

    override suspend fun read(): Int = semaphore.withReadPermit { state }
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public class SharedStateRWLong(private var state: Long, private val semaphore: ReadWriteSemaphore):
    SharedState<Long, Long> {
    public constructor(state: Long, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    override suspend fun beginRead(): Long {
        semaphore.acquireReadPermit()
        return state
    }
    override suspend fun beginWrite(): Long {
        semaphore.acquireWritePermit()
        return state
    }

    override suspend fun finishRead() {
        semaphore.releaseReadPermit()
    }

    override suspend fun finishWrite(state: Long) {
        this.state = state
        semaphore.releaseWritePermit()
    }

    override suspend fun read(): Long = semaphore.withReadPermit { state }
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public class SharedStateRWBoolean(private var state: Boolean, private val semaphore: ReadWriteSemaphore):
    SharedState<Boolean, Boolean> {
    public constructor(state: Boolean, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    override suspend fun beginRead(): Boolean {
        semaphore.acquireReadPermit()
        return state
    }
    override suspend fun beginWrite(): Boolean {
        semaphore.acquireWritePermit()
        return state
    }

    override suspend fun finishRead() {
        semaphore.releaseReadPermit()
    }

    override suspend fun finishWrite(state: Boolean) {
        this.state = state
        semaphore.releaseWritePermit()
    }

    override suspend fun read(): Boolean = semaphore.withReadPermit { state }
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public class SharedStateRWString(private var state: String, private val semaphore: ReadWriteSemaphore):
    SharedState<String, String> {
    public constructor(state: String, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    override suspend fun beginRead(): String {
        semaphore.acquireReadPermit()
        return state
    }
    override suspend fun beginWrite(): String {
        semaphore.acquireWritePermit()
        return state
    }

    override suspend fun finishRead() {
        semaphore.releaseReadPermit()
    }

    override suspend fun finishWrite(state: String) {
        this.state = state
        semaphore.releaseWritePermit()
    }

    override suspend fun read(): String = semaphore.withReadPermit { state }
}