package org.kornea.toolkit.common

import org.abimon.kornea.annotations.AvailableSince
import org.abimon.kornea.annotations.ExperimentalKorneaToolkit

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0)
public class SharedStateRWInt(private var state: Int, private val semaphore: ReadWriteSemaphore): SharedState<Int, Int> {
    public constructor(state: Int, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public suspend fun accessState(): Int = semaphore.withReadPermit { state }

    public override suspend fun <R> accessState(block: suspend (Int) -> R): R =
        semaphore.withReadPermit { block(state) }

    public override suspend fun mutateState(block: suspend (Int) -> Int): SharedStateRWInt {
        semaphore.withWritePermit { state = block(state) }

        return this
    }
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0)
public class SharedStateRWLong(private var state: Long, private val semaphore: ReadWriteSemaphore): SharedState<Long, Long> {
    public constructor(state: Long, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public suspend fun accessState(): Long = semaphore.withReadPermit { state }

    public override suspend fun <R> accessState(block: suspend (Long) -> R): R =
        semaphore.withReadPermit { block(state) }

    public override suspend fun mutateState(block: suspend (Long) -> Long): SharedStateRWLong {
        semaphore.withWritePermit { state = block(state) }

        return this
    }
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0)
public class SharedStateRWBoolean(private var state: Boolean, private val semaphore: ReadWriteSemaphore): SharedState<Boolean, Boolean> {
    public constructor(state: Boolean, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public suspend fun accessState(): Boolean = semaphore.withReadPermit { state }

    public override suspend fun <R> accessState(block: suspend (Boolean) -> R): R =
        semaphore.withReadPermit { block(state) }

    public override suspend fun mutateState(block: suspend (Boolean) -> Boolean): SharedStateRWBoolean {
        semaphore.withWritePermit { state = block(state) }

        return this
    }
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0)
public class SharedStateRWString(private var state: String, private val semaphore: ReadWriteSemaphore): SharedState<String, String> {
    public constructor(state: String, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public suspend fun accessState(): String = semaphore.withReadPermit { state }

    public override suspend fun <R> accessState(block: suspend (String) -> R): R =
        semaphore.withReadPermit { block(state) }

    public override suspend fun mutateState(block: suspend (String) -> String): SharedStateRWString {
        semaphore.withWritePermit { state = block(state) }

        return this
    }
}