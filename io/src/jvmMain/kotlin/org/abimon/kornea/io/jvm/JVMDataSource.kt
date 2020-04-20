package org.abimon.kornea.io.jvm

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.DataSource
import org.abimon.kornea.io.common.DataSourceReproducibility
import org.abimon.kornea.io.common.addCloseHandler
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import java.io.InputStream

@ExperimentalUnsignedTypes
class JVMDataSource(val func: () -> InputStream, override val location: String? = null) : DataSource<JVMInputFlow> {
    override val dataSize: ULong? = null
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    /**
     * The reproducibility traits of this data source.
     *
     * These traits *may* change between invocations, so a fresh instance should be obtained each time
     */
    override val reproducibility: DataSourceReproducibility
        get() = DataSourceReproducibility(
            isUnreliable = true
        )

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<JVMInputFlow> =
        if (!closed) KorneaResult.Success(JVMInputFlow(func(), location ?: this.location))
        else KorneaResult.Failure(DataSource.ERRORS_SOURCE_CLOSED, "Instance closed")

    override suspend fun canOpenInputFlow(): Boolean = !closed

    override suspend fun close() {
        super.close()

        closed = true
    }
}