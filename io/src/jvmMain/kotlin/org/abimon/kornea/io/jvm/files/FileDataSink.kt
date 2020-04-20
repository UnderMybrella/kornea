package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import java.io.File

@ExperimentalUnsignedTypes
class FileDataSink(val backing: File): DataSink<FileOutputFlow> {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<FileOutputFlow> = ArrayList(1)
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun openOutputFlow(): KorneaResult<FileOutputFlow> {
        when {
            closed -> return KorneaResult.Failure(DataSource.ERRORS_SOURCE_CLOSED, "Instance closed")
            canOpenOutputFlow() -> {
                val stream = FileOutputFlow(backing)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                return KorneaResult.Success(stream)
            }
            else -> return KorneaResult.Failure(
                DataSource.ERRORS_TOO_MANY_SOURCES_OPEN,
                "Too many instances open (${openInstances.size}/1)"
            )
        }
    }

    override suspend fun canOpenOutputFlow(): Boolean = !closed && (openInstances.size < 1)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is FileOutputFlow) {
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