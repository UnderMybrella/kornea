package org.kornea.toolkit.common

import org.abimon.kornea.annotations.AvailableSince
import org.abimon.kornea.annotations.ExperimentalKorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_1_3_0)
public interface SharedState<I, M> {
    @ExperimentalKorneaToolkit
    public companion object {
        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun of(starting: Int): SharedState<Int, Int> = SharedStateRWInt(starting)
        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun of(starting: Long): SharedState<Long, Long> = SharedStateRWLong(starting)
        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun of(starting: Boolean): SharedState<Boolean, Boolean> = SharedStateRWBoolean(starting)
        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun of(starting: String): SharedState<String, String> = SharedStateRWString(starting)

        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun <T> of(starting: MutableList<T>): SharedState<ImmutableListView<T>, MutableList<T>> = SharedStateRWMutability(KorneaMutableList(starting))
        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun of(starting: StringBuilder): SharedState<String, StringBuilder> = SharedStateRWMutability(KorneaStringBuilder(starting))

        @AvailableSince(KorneaToolkit.VERSION_1_3_0)
        public inline fun <T> of(starting: T): SharedState<T, T> = SharedStateRW(starting)
    }

    public suspend fun <R> accessState(block: suspend (I) -> R): R
    public suspend fun mutateState(block: suspend (M) -> M): SharedState<I, M>
}