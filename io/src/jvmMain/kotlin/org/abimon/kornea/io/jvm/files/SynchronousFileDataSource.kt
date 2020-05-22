package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceClosed
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceUnknown
import org.abimon.kornea.io.common.DataSource.Companion.korneaTooManySourcesOpen
import java.io.File
import kotlin.math.max

@ExperimentalUnsignedTypes
class SynchronousFileDataSource(
    val backing: File,
    val maxInstanceCount: Int = -1,
    override val location: String? = backing.absolutePath
) : DataSource<SynchronousFileInputFlow> {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    override val dataSize: ULong
        get() = backing.length().toULong()

    private val openInstances: MutableList<SynchronousFileInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<SynchronousFileInputFlow> =
        when {
            closed -> korneaSourceClosed()
            maxInstanceCount != -1 && openInstances.size >= maxInstanceCount ->
                korneaTooManySourcesOpen(maxInstanceCount)
            canOpenInputFlow() -> {
                val stream = SynchronousFileInputFlow(backing, location ?: this.location)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                KorneaResult.Success(stream)
            }
            else -> korneaSourceUnknown()
        }

    override suspend fun canOpenInputFlow(): Boolean =
        !closed && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is SynchronousFileInputFlow) {
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