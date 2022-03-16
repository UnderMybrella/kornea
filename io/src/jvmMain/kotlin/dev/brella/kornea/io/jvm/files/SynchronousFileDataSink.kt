package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.base.common.ObservableDataCloseable
import dev.brella.kornea.base.common.closeAll
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.DataSink
import dev.brella.kornea.io.common.DataSink.Companion.korneaSinkClosed
import dev.brella.kornea.io.common.DataSink.Companion.korneaSinkUnknown
import dev.brella.kornea.io.common.DataSink.Companion.korneaTooManySinksOpen
import dev.brella.kornea.io.common.Uri
import java.io.File

public class SynchronousFileDataSink(public val backing: File) : BaseDataCloseable(),
    DataSink<SynchronousFileOutputFlow> {
    private val openInstances: MutableList<SynchronousFileOutputFlow> = ArrayList(1)

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.success(Uri.fromUri(backing.toURI()))

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