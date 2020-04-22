package org.abimon.kornea.io.common

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.DataSource.Companion.ERRORS_SOURCE_CLOSED
import org.abimon.kornea.io.common.DataSource.Companion.ERRORS_TOO_MANY_SOURCES_OPEN
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import kotlin.math.max

@ExperimentalUnsignedTypes
class BinaryDataSource(
    val byteArray: ByteArray,
    val maxInstanceCount: Int = -1,
    override val location: String? = null
) :
    DataSource<BinaryInputFlow> {
    companion object {}

    override val dataSize: ULong
        get() = byteArray.size.toULong()
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<BinaryInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> {
        when {
            closed -> return KorneaResult.Error(ERRORS_SOURCE_CLOSED, "Instance closed")
            canOpenInputFlow() -> {
                val stream = BinaryInputFlow(byteArray, location = location ?: this.location)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                return KorneaResult.Success(stream)
            }
            else -> return KorneaResult.Error(
                ERRORS_TOO_MANY_SOURCES_OPEN,
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

    override suspend fun close() {
        super.close()

        if (!closed) {
            closed = true
            openInstances.toTypedArray().closeAll()
            openInstances.clear()
        }
    }
}