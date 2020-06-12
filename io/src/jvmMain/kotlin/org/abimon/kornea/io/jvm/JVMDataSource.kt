package org.abimon.kornea.io.jvm

import org.abimon.kornea.errors.common.KorneaResult
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.DataSource
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceClosed
import org.abimon.kornea.io.common.DataSourceReproducibility
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
        if (!closed) KorneaResult.success(JVMInputFlow(func(), location ?: this.location))
        else korneaSourceClosed()

    override suspend fun canOpenInputFlow(): Boolean = !closed

    override suspend fun close() {
        super.close()

        closed = true
    }
}