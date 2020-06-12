package org.abimon.kornea.io.common

import org.abimon.kornea.errors.common.KorneaResult
import org.abimon.kornea.errors.common.map
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceClosed
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceUnknown
import org.abimon.kornea.io.common.DataSource.Companion.korneaTooManySourcesOpen
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

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<WindowedInputFlow> {
        when {
            closed || parent.isClosed -> return korneaSourceClosed()
            openInstances.size == maxInstanceCount -> return korneaTooManySourcesOpen(maxInstanceCount)
            canOpenInputFlow() -> return parent.openInputFlow().map { parentFlow ->
                val flow = WindowedInputFlow(parentFlow, windowOffset, windowSize, location ?: this.location)
                flow.addCloseHandler(this::instanceClosed)
                openInstances.add(flow)

                flow
            }
            else -> return korneaSourceUnknown()
        }
    }

    override suspend fun canOpenInputFlow(): Boolean =
        !closed && parent.canOpenInputFlow() && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    @Suppress("RedundantSuspendModifier")
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