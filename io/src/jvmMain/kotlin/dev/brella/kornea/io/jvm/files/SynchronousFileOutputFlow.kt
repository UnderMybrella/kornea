package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.flow.CountingOutputFlow
import dev.brella.kornea.io.common.flow.PrintOutputFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.File
import java.io.FileOutputStream

@ExperimentalUnsignedTypes
public class SynchronousFileOutputFlow(public val backing: File) : BaseDataCloseable(), CountingOutputFlow, PrintOutputFlow {
    private val stream = FileOutputStream(backing)
    private val channel = stream.channel
    override val streamOffset: Long
        get() = channel.position()

    override suspend fun write(byte: Int): Unit = runInterruptible(Dispatchers.IO) { stream.write(byte) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit =
        runInterruptible(Dispatchers.IO) { stream.write(b, off, len) }

    override suspend fun flush(): Unit = runInterruptible(Dispatchers.IO) { stream.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        runInterruptible(Dispatchers.IO) { stream.close() }
    }
}