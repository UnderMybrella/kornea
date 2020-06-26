package dev.brella.kornea.io.common

import dev.brella.kornea.io.common.DataCloseableEventHandler
import dev.brella.kornea.io.common.ObservableDataCloseable
import dev.brella.kornea.toolkit.common.ImmutableListView

@ExperimentalUnsignedTypes
public abstract class BaseDataCloseable: ObservableDataCloseable {
    protected open val mutableCloseHandlers: MutableList<DataCloseableEventHandler> by lazy { ArrayList() }
    override val closeHandlers: List<DataCloseableEventHandler> by lazy { ImmutableListView(mutableCloseHandlers) }

    protected var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean = mutableCloseHandlers.add(handler)

    override suspend fun close() {
        if (!closed) {
            super.close()
            closed = true

            whenClosed()
        }
    }

    public open suspend fun whenClosed() {}
}