package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public interface KorneaMutability<M, I> {
    public suspend fun asMutable(): M
    public suspend fun asImmutable(): I
}

@ExperimentalKorneaToolkit("ReadWriteSemaphores are quite fragile, beware")
@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public class SharedStateRWMutability<T: KorneaMutability<M, I>, M, I>(private var state: T, private val semaphore: ReadWriteSemaphore): SharedState<I, M> {
    public constructor(state: T, permitLimit: Int = 8): this(state, ReadWriteSemaphore(permitLimit))

    public override suspend fun <R> accessState(block: suspend (I) -> R): R =
        semaphore.withReadPermit { block(state.asImmutable()) }

    public override suspend fun mutateState(block: suspend (M) -> M): SharedStateRWMutability<T, M, I> {
        semaphore.withWritePermit { block(state.asMutable()) }

        return this
    }
}

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline class KorneaMutableList<T>(private val list: MutableList<T>): KorneaMutability<MutableList<T>, ImmutableListView<T>> {
    override suspend fun asMutable(): MutableList<T> = list
    override suspend fun asImmutable(): ImmutableListView<T> = ImmutableListView(list)
}

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public inline class KorneaStringBuilder(private val builder: StringBuilder): KorneaMutability<StringBuilder, String> {
    override suspend fun asMutable(): StringBuilder = builder
    override suspend fun asImmutable(): String = builder.toString()
}