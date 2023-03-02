package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.composite.common.Composite
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.IntFlowState
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.io.common.flow.OutputFlowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.File
import java.io.FileOutputStream

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class SynchronousFileOutputFlow(
    public val backing: File,
    override val location: String? = "SynchronousFileOutputFlow(${backing})"
) : BaseDataCloseable(), OutputFlow, OutputFlowState, IntFlowState by IntFlowState.base(), Composite.Empty {
    private val stream = FileOutputStream(backing)
    private val channel = stream.channel

    override suspend fun position(): ULong =
        runInterruptible(Dispatchers.IO) { channel.position().toULong() }

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.success(Uri.fromFile(backing))

    override suspend fun write(byte: Int): Unit = runInterruptible(Dispatchers.IO) { stream.write(byte) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit =
        runInterruptible(Dispatchers.IO) { stream.write(b, off, len) }

    override suspend fun flush(): Unit = runInterruptible(Dispatchers.IO) { stream.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        runInterruptible(Dispatchers.IO) { stream.close() }
    }
}