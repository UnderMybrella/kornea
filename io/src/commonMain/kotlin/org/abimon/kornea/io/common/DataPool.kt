package org.abimon.kornea.io.common

import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.OutputFlow

@ExperimentalUnsignedTypes
public interface DataPool<I : InputFlow, O : OutputFlow> : DataSource<I>, DataSink<O> {
    override val isClosed: Boolean
    override val closeHandlers: List<DataCloseableEventHandler>
    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean
    override suspend fun close()
}