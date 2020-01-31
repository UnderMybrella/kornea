package org.abimon.kornea.io.common

import org.abimon.kornea.io.common.flow.OutputFlow

/**
* An interface that loosely defines a destination for data.
 */
@ExperimentalUnsignedTypes
interface DataSink<O: OutputFlow>: ObservableDataCloseable {
    suspend fun openOutputFlow(): O?
    suspend fun canOpenOutputFlow(): Boolean
}