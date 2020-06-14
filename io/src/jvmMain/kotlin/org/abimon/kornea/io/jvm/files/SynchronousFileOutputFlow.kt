package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.BaseDataCloseable
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.CountingOutputFlow
import java.io.File
import java.io.FileOutputStream

@ExperimentalUnsignedTypes
public class SynchronousFileOutputFlow(public val backing: File) : BaseDataCloseable(), CountingOutputFlow {
    private val stream = FileOutputStream(backing)
    private val channel = stream.channel
    override val streamOffset: Long
        get() = channel.position()

    override suspend fun write(byte: Int): Unit = withContext(Dispatchers.IO) { stream.write(byte) }
    override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit =
        withContext(Dispatchers.IO) { stream.write(b, off, len) }

    override suspend fun flush(): Unit = withContext(Dispatchers.IO) { stream.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        withContext(Dispatchers.IO) { stream.close() }
    }
}