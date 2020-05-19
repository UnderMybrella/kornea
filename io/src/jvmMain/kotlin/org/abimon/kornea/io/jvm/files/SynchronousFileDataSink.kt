package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.*
import java.io.File

@ExperimentalUnsignedTypes
class SynchronousFileDataSink(val backing: File): DataSink<SynchronousFileOutputFlow> {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<SynchronousFileOutputFlow> = ArrayList(1)
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun openOutputFlow(): SynchronousFileOutputFlow? {
        if (canOpenOutputFlow()) {
            val stream = SynchronousFileOutputFlow(backing)
            stream.addCloseHandler(this::instanceClosed)
            openInstances.add(stream)
            return stream
        } else {
            return null
        }
    }

    override suspend fun canOpenOutputFlow(): Boolean = !closed && (openInstances.size < 1)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is SynchronousFileOutputFlow) {
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