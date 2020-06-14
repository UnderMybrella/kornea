package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.errors.common.KorneaResult
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkClosed
import org.abimon.kornea.io.common.DataSink.Companion.korneaTooManySinksOpen
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkUnknown
import java.io.File

@ExperimentalUnsignedTypes
public class SynchronousFileDataSink(public val backing: File) : BaseDataCloseable(),
    DataSink<SynchronousFileOutputFlow> {
    private val openInstances: MutableList<SynchronousFileOutputFlow> = ArrayList(1)

    override suspend fun openOutputFlow(): KorneaResult<SynchronousFileOutputFlow> =
        when {
            closed -> korneaSinkClosed()
            openInstances.isNotEmpty() -> korneaTooManySinksOpen(1)

            canOpenOutputFlow() -> {
                val stream = SynchronousFileOutputFlow(backing)
                stream.registerCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                KorneaResult.success(stream)
            }
            else -> korneaSinkUnknown()
        }

    override suspend fun canOpenOutputFlow(): Boolean = !closed && (openInstances.size < 1)

    @Suppress("RedundantSuspendModifier")
    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is SynchronousFileOutputFlow) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        openInstances.closeAll()
        openInstances.clear()
    }
}