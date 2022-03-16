package dev.brella.kornea.io.jvm

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.IntFlowState
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.io.common.flow.OutputFlowState
import dev.brella.kornea.io.common.flow.PrintOutputFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.OutputStream

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public open class JVMOutputFlow(protected val stream: OutputStream) : BaseDataCloseable(), OutputFlow, PrintOutputFlow,
    OutputFlowState, IntFlowState by IntFlowState.base() {
    override suspend fun write(byte: Int): Unit = runInterruptible(Dispatchers.IO) { stream.write(byte) }
    override suspend fun write(b: ByteArray): Unit = runInterruptible(Dispatchers.IO) { stream.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit =
        runInterruptible(Dispatchers.IO) { stream.write(b, off, len) }

    override suspend fun flush(): Unit = runInterruptible(Dispatchers.IO) { stream.flush() }

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    override suspend fun whenClosed() {
        super.whenClosed()

        runInterruptible(Dispatchers.IO) { stream.close() }
    }
}