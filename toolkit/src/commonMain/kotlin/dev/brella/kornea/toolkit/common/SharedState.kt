package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
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

    public suspend fun <R> accessState(block: suspend (I) -> R): R
    public suspend fun mutateState(block: suspend (M) -> M): SharedState<I, M>
}