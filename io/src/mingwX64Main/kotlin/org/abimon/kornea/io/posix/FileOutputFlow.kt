package org.abimon.kornea.io.posix

import kotlinx.cinterop.CPointer
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.CountingOutputFlow
import platform.posix.FILE

@ExperimentalUnsignedTypes
class FileOutputFlow(val fp: FilePointer) : CountingOutputFlow {
    constructor(fp: CPointer<FILE>) : this(FilePointer(fp))

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    override val streamOffset: Long
        get() = fp.pos()

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    private suspend fun <T> io(block: suspend () -> T): T = block()

    override suspend fun write(byte: Int) = io { fp.write(byte) }
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        io { fp.write(b, off, len) }
    }

    override suspend fun flush() {
        io { fp.flush() }
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            io { fp.close() }
            closed = true
        }
    }
}