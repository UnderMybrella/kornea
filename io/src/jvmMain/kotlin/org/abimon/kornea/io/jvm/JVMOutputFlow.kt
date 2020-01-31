package org.abimon.kornea.io.jvm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.OutputFlow
import java.io.OutputStream

@ExperimentalUnsignedTypes
open class JVMOutputFlow(val stream: OutputStream): OutputFlow {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun write(byte: Int) = withContext(Dispatchers.IO) { stream.write(byte) }
    override suspend fun write(b: ByteArray) = withContext(Dispatchers.IO) { stream.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int) = withContext(Dispatchers.IO) { stream.write(b, off, len) }
    override suspend fun flush() = withContext(Dispatchers.IO) { stream.flush() }

    override suspend fun close() {
        super.close()

        if (!closed) {
            withContext(Dispatchers.IO) { stream.close() }
            closed = true
        }
    }
}