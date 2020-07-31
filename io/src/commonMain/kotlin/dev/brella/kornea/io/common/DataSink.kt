package dev.brella.kornea.io.common

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.toolkit.common.ObservableDataCloseable

/**
* An interface that loosely defines a destination for data.
 */
@ExperimentalUnsignedTypes
public interface DataSink<out O: OutputFlow>: ObservableDataCloseable {
    public companion object {
        public const val ERRORS_SINK_CLOSED: Int = 0x1000
        public const val ERRORS_TOO_MANY_FLOWS_OPEN: Int = 0x1001
        public const val ERRORS_UNKNOWN: Int = 0x1FFF

        @ExperimentalUnsignedTypes
        public inline fun <reified T> korneaSinkClosed(message: String = "Sink closed"): KorneaResult<T> =
            KorneaResult.errorAsIllegalState(ERRORS_SINK_CLOSED, message)

        @ExperimentalUnsignedTypes
        public inline fun <reified T> korneaTooManySinksOpen(capacity: Int): KorneaResult<T> = korneaTooManySinksOpen("Too many flows open (Capacity: $capacity)")
        @ExperimentalUnsignedTypes
        public inline fun <reified T> korneaTooManySinksOpen(message: String): KorneaResult<T> =
            KorneaResult.errorAsIllegalState(ERRORS_TOO_MANY_FLOWS_OPEN, message)

        @ExperimentalUnsignedTypes
        public inline fun <reified T> korneaSinkUnknown(message: String = "An unknown error has occurred"): KorneaResult<T> =
            KorneaResult.errorAsIllegalState(ERRORS_UNKNOWN, message)
    }

    public suspend fun openOutputFlow(): KorneaResult<O>
    public suspend fun canOpenOutputFlow(): Boolean
}