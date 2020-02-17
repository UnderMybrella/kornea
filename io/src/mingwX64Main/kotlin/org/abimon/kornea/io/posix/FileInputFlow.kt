package org.abimon.kornea.io.posix

import kotlinx.cinterop.CPointer
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.InputFlow
import platform.posix.FILE
import platform.posix.SEEK_CUR
import platform.posix.SEEK_END
import platform.posix.SEEK_SET

@ExperimentalUnsignedTypes
class FileInputFlow(val fp: FilePointer): InputFlow {
    constructor(fp: CPointer<FILE>): this(FilePointer(fp))

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed
    private val size = fp.size()

    private suspend fun <T> io(block: suspend () -> T): T = block()

    override suspend fun read(): Int? = io { fp.read() }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        return io { fp.read(b, off, len).toInt() }
    }

    override suspend fun skip(n: ULong): ULong? = io {
        val pos = fp.pos()
        if (fp.seek(n.toLong(), SEEK_CUR) != 0) return@io null
        return@io (fp.pos() - pos).toULong()
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = size.toULong()
    override suspend fun position(): ULong = io { fp.pos().toULong() }

    override suspend fun seek(pos: Long, mode: Int): ULong? {
        when (mode) {
            InputFlow.FROM_BEGINNING -> io { fp.seek(pos, SEEK_SET) }
            InputFlow.FROM_POSITION -> io { fp.seek(pos, SEEK_CUR) }
            InputFlow.FROM_END -> io { fp.seek(pos, SEEK_END) }
            else -> return null
        }

        return position()
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            io { fp.close() }
            closed = true
        }
    }
}