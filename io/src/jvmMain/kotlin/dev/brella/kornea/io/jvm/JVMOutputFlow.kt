package dev.brella.kornea.io.jvm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.DataCloseableEventHandler
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.io.common.flow.PrintOutputFlow
import java.io.OutputStream

@ExperimentalUnsignedTypes
public open class JVMOutputFlow(protected val stream: OutputStream): BaseDataCloseable(), OutputFlow, PrintOutputFlow {
    override suspend fun write(byte: Int): Unit = withContext(Dispatchers.IO) { stream.write(byte) }
    override suspend fun write(b: ByteArray): Unit = withContext(Dispatchers.IO) { stream.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit = withContext(Dispatchers.IO) { stream.write(b, off, len) }
    override suspend fun flush(): Unit = withContext(Dispatchers.IO) { stream.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        withContext(Dispatchers.IO) { stream.close() }
    }
}