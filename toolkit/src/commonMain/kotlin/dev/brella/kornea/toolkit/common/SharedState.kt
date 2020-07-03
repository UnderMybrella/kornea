package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
public interface SharedState<I, M> {
    @ExperimentalKorneaToolkit
    public companion object {
        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun of(starting: Int): SharedState<Int, Int> = SharedStateRWInt(starting)
        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun of(starting: Long): SharedState<Long, Long> = SharedStateRWLong(starting)
        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun of(starting: Boolean): SharedState<Boolean, Boolean> = SharedStateRWBoolean(starting)
        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun of(starting: String): SharedState<String, String> = SharedStateRWString(starting)

        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun <T> of(starting: MutableList<T>): SharedState<ImmutableListView<T>, MutableList<T>> = SharedStateRWMutability(KorneaMutableList(starting))
        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun of(starting: StringBuilder): SharedState<String, StringBuilder> = SharedStateRWMutability(KorneaStringBuilder(starting))

        @AvailableSince(KorneaToolkit.VERSION_1_3_0_INDEV)
        public inline fun <T> of(starting: T): SharedState<T, T> = SharedStateRW(starting)
    }

    @AvailableSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
    public suspend fun read(): I

    @AvailableSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
    public suspend fun beginRead(): I
    @AvailableSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
    public suspend fun beginWrite(): M

    @AvailableSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
    public suspend fun finishRead()
    @AvailableSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
    public suspend fun finishWrite(state: M)
}

@ChangedSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
public suspend inline fun <R, I> SharedState<I, *>.accessState(block: (I) -> R): R {
    try {
        return block(beginRead())
    } finally {
        finishRead()
    }
}

@ChangedSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
public suspend inline fun <I, M> SharedState<I, M>.mutateState(block: (M) -> M): SharedState<I, M> {
    var state = beginWrite()

    try {
        state = block(state)

        return this
    } finally {
        finishWrite(state)
    }
}

@AvailableSince(KorneaToolkit.VERSION_1_2_0_ALPHA)
public suspend inline fun <M, R> SharedState<*, M>.mutateStateWithResult(block: (M) -> Pair<M, R>): R {
    var state = beginWrite()

    try {
        val pair = block(state)
        state = pair.first
        return pair.second
    } finally {
        finishWrite(state)
    }
}
