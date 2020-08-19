package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.toolkit.common.ImmutableListView
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.SharedState

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

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun SharedState.Companion.of(starting: Int): SharedState<Int, Int> = SharedStateRWInt(starting)
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun SharedState.Companion.of(starting: Long): SharedState<Long, Long> = SharedStateRWLong(starting)
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun SharedState.Companion.of(starting: Boolean): SharedState<Boolean, Boolean> = SharedStateRWBoolean(starting)
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun SharedState.Companion.of(starting: String): SharedState<String, String> = SharedStateRWString(starting)

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun <T> SharedState.Companion.of(starting: MutableList<T>): SharedState<ImmutableListView<T>, MutableList<T>> = SharedStateRWMutability(KorneaMutableList(starting))
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun SharedState.Companion.of(starting: StringBuilder): SharedState<String, StringBuilder> = SharedStateRWMutability(KorneaStringBuilder(starting))

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline fun <T> SharedState.Companion.of(starting: T): SharedState<T, T> = SharedStateRW(starting)