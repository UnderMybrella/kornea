package org.abimon.kornea.io.common

import org.abimon.kornea.io.common.flow.WindowedInputFlow
import kotlin.math.max

@ExperimentalUnsignedTypes
open class WindowedDataSource(
    val parent: DataSource<*>,
    val windowOffset: ULong,
    val windowSize: ULong,
    val maxInstanceCount: Int = -1,
    val closeParent: Boolean = true,
    override val location: String? =
        "${parent.location}[${windowOffset.toString(16).toUpperCase()}h,${windowOffset.plus(windowSize).toString(16)
            .toUpperCase()}h]"
) : DataSource<WindowedInputFlow> {
    companion object {}

    override val dataSize: ULong?
        get() = parent.dataSize?.minus(windowOffset)?.coerceAtMost(windowSize)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    private val openInstances: MutableList<WindowedInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility
        get() = parent.reproducibility or DataSourceReproducibility.DETERMINISTIC_MASK

    override suspend fun openNamedInputFlow(location: String?): WindowedInputFlow? {
        if (canOpenInputFlow()) {
            val parentFlow = parent.openInputFlow() ?: return null
            val flow = WindowedInputFlow(parentFlow, windowOffset, windowSize, location ?: this.location)
            flow.addCloseHandler(this::instanceClosed)
            openInstances.add(flow)
            return flow
        } else {
            return null
        }
    }

    override suspend fun canOpenInputFlow(): Boolean =
        !closed && parent.canOpenInputFlow() && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is WindowedInputFlow) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun close() {
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