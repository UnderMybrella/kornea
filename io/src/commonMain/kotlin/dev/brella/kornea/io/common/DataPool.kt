package dev.brella.kornea.io.common

import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.toolkit.common.DataCloseableEventHandler

@ExperimentalUnsignedTypes
public interface DataPool<out I : InputFlow, out O : OutputFlow> : DataSource<I>, DataSink<O> {
    override val isClosed: Boolean
    override val closeHandlers: List<DataCloseableEventHandler>
    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean
    override suspend fun close()
}