package org.abimon.kornea.io.common

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.flow.OutputFlow

/**
* An interface that loosely defines a destination for data.
 */
@ExperimentalUnsignedTypes
interface DataSink<O: OutputFlow>: ObservableDataCloseable {
    companion object {
        const val ERRORS_SINK_CLOSED = 0x1000
        const val ERRORS_TOO_MANY_FLOWS_OPEN = 0x1001
        const val ERRORS_UNKNOWN = 0x1FFF

        @ExperimentalUnsignedTypes
        inline fun <reified T> korneaSinkClosed(message: String = "Sink closed"): KorneaResult.Error<T, Unit> =
            KorneaResult.Error(ERRORS_SINK_CLOSED, message)

        @ExperimentalUnsignedTypes
        inline fun <reified T> korneaTooManySinksOpen(capacity: Int): KorneaResult.Error<T, Unit> = korneaTooManySinksOpen("Too many flows open (Capacity: $capacity)")
        @ExperimentalUnsignedTypes
        inline fun <reified T> korneaTooManySinksOpen(message: String): KorneaResult.Error<T, Unit> =
            KorneaResult.Error(ERRORS_TOO_MANY_FLOWS_OPEN, message)

        @ExperimentalUnsignedTypes
        inline fun <reified T> korneaSinkUnknown(message: String = "An unknown error has occurred"): KorneaResult.Error<T, Unit> =
            KorneaResult.Error(ERRORS_UNKNOWN, message)
    }

    suspend fun openOutputFlow(): KorneaResult<O>
    suspend fun canOpenOutputFlow(): Boolean
}