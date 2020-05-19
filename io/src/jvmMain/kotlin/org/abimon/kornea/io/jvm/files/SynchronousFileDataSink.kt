package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkClosed
import org.abimon.kornea.io.common.DataSink.Companion.korneaTooManySinksOpen
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkUnknown
import java.io.File

@ExperimentalUnsignedTypes
class SynchronousFileDataSink(val backing: File) : DataSink<SynchronousFileOutputFlow> {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<SynchronousFileOutputFlow> = ArrayList(1)
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun openOutputFlow(): KorneaResult<SynchronousFileOutputFlow> =
        when {
            closed -> korneaSinkClosed()
            openInstances.isNotEmpty() -> korneaTooManySinksOpen(1)
            canOpenOutputFlow() -> {
                val stream = SynchronousFileOutputFlow(backing)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                KorneaResult.Success(stream)
            }
            else -> korneaSinkUnknown()
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