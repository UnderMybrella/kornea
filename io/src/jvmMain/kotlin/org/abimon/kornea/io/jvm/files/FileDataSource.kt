package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.erorrs.common.korneaNotFound
import org.abimon.kornea.io.common.*
import java.io.File
import kotlin.math.max

@ExperimentalUnsignedTypes
class FileDataSource(val backing: File, val maxInstanceCount: Int = -1, override val location: String? = backing.absolutePath): DataSource<FileInputFlow> {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    override val dataSize: ULong
        get() = backing.length().toULong()

    private val openInstances: MutableList<FileInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility = DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<FileInputFlow> {
        when {
            closed -> return KorneaResult.Error(DataSource.ERRORS_SOURCE_CLOSED, "Instance closed")
            !backing.exists() -> return korneaNotFound("$backing does not exist")
            canOpenInputFlow() -> {
                val stream = FileInputFlow(backing, location ?: this.location)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                return KorneaResult.Success(stream)
            }
            else -> return KorneaResult.Error(
                DataSource.ERRORS_TOO_MANY_SOURCES_OPEN,
                "Too many instances open (${openInstances.size}/${maxInstanceCount})"
            )
        }
    }

    override suspend fun canOpenInputFlow(): Boolean = !closed && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is FileInputFlow) {
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