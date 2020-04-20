package org.abimon.kornea.io.common

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.flow.OutputFlow

/**
* An interface that loosely defines a destination for data.
 */
@ExperimentalUnsignedTypes
interface DataSink<O: OutputFlow>: ObservableDataCloseable {
    companion object {
        const val ERRORS_SINK_CLOSED = 0x00
    }

    suspend fun openOutputFlow(): KorneaResult<O>
    suspend fun canOpenOutputFlow(): Boolean
}