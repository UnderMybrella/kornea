package org.abimon.kornea.io.common

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.DataSink.Companion.ERRORS_SINK_CLOSED
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import org.abimon.kornea.io.common.flow.BinaryOutputFlow
import kotlin.math.max

@ExperimentalUnsignedTypes
class BinaryDataPool(
    override val location: String? = null,
    val output: BinaryOutputFlow = BinaryOutputFlow(),
    val maxInstanceCount: Int = -1
) :
    DataPool<BinaryInputFlow, BinaryOutputFlow> {
    override val dataSize: ULong?
        get() = output.getDataSize()

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isDeterministic = true, isRandomAccess = true)
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<BinaryInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    private var outputClosed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> {
        when {
            closed -> return KorneaResult.Error(DataSource.ERRORS_SOURCE_CLOSED, "Instance closed")
            canOpenInputFlow() -> {
                val stream = BinaryInputFlow(output.getData(), location = location ?: this.location)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                return KorneaResult.Success(stream)
            }
            else -> return KorneaResult.Error(
                DataSource.ERRORS_TOO_MANY_FLOWS_OPEN,
                "Too many instances open (${openInstances.size}/${maxInstanceCount})"
            )
        }
    }

    override suspend fun canOpenInputFlow(): Boolean =
        !closed && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is BinaryInputFlow) {
            openInstances.remove(closeable)
        }
    }

    private suspend fun onOutputClosed(closeable: ObservableDataCloseable) {
        if (closeable is BinaryOutputFlow) {
            outputClosed = true
            output.close()
        }
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            closed = true
            openInstances.toTypedArray().closeAll()
            openInstances.clear()
        }
    }

    override suspend fun openOutputFlow(): KorneaResult<BinaryOutputFlow> =
        if (canOpenOutputFlow()) KorneaResult.Success(output)
        else KorneaResult.Error(ERRORS_SINK_CLOSED, "Sink closed")

    override suspend fun canOpenOutputFlow(): Boolean = !outputClosed

    init {
        output.addCloseHandler(this::onOutputClosed)
    }
}