package org.abimon.kornea.io.common

import org.abimon.kornea.io.common.flow.SinkOffsetInputFlow
import kotlin.math.max

@ExperimentalUnsignedTypes
open class OffsetDataSource(val parent: DataSource<*>, val offset: ULong, val maxInstanceCount: Int = -1, val closeParent: Boolean = true,
                            override val location: String? = "${parent.location}+${offset.toString(16).toUpperCase()}h") :
    DataSource<SinkOffsetInputFlow> {
    companion object {}

    override val dataSize: ULong?
        get() = parent.dataSize?.minus(offset)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    private val openInstances: MutableList<SinkOffsetInputFlow> = ArrayList(max(maxInstanceCount, 0))

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility
        get() = parent.reproducibility or DataSourceReproducibility.DETERMINISTIC_MASK

    override suspend fun openNamedInputFlow(location: String?): SinkOffsetInputFlow? {
        if (canOpenInputFlow()) {
            val parentFlow = parent.openInputFlow() ?: return null
            val flow = SinkOffsetInputFlow(parentFlow, offset, location ?: this.location)
            flow.addCloseHandler(this::instanceClosed)
            openInstances.add(flow)
            return flow
        } else {
            return null
        }
    }

    override suspend fun canOpenInputFlow(): Boolean = !closed && parent.canOpenInputFlow() && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is SinkOffsetInputFlow) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            closed = true
            openInstances.toTypedArray().closeAll()
            openInstances.clear()

            if (closeParent) {
                parent.close()
            }
        }
    }
}