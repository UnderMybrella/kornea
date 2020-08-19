package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.toolkit.common.ImmutableListView
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.SharedState

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public interface KorneaMutability<M, I> {
    public suspend fun asMutable(): M
    public suspend fun asImmutable(): I
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public class SharedStateRWMutability<T: KorneaMutability<M, I>, M, I>(private val state: T, private val semaphore: ReadWriteSemaphore):
    SharedState<I, M> {
    public constructor(state: T, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    override suspend fun beginRead(): I {
        semaphore.acquireReadPermit()
        return state.asImmutable()
    }
    override suspend fun beginWrite(): M {
        semaphore.acquireWritePermit()
        return state.asMutable()
    }

    override suspend fun finishRead() {
        semaphore.releaseReadPermit()
    }

    override suspend fun finishWrite(state: M) {
//        this.state = state
        semaphore.releaseWritePermit()
    }

    override suspend fun read(): I = semaphore.withReadPermit { state.asImmutable() }
}

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline class KorneaMutableList<T>(private val list: MutableList<T>):
    KorneaMutability<MutableList<T>, ImmutableListView<T>> {
    override suspend fun asMutable(): MutableList<T> = list
    override suspend fun asImmutable(): ImmutableListView<T> = ImmutableListView(list)
}

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline class KorneaStringBuilder(private val builder: StringBuilder): KorneaMutability<StringBuilder, String> {
    override suspend fun asMutable(): StringBuilder = builder
    override suspend fun asImmutable(): String = builder.toString()
}