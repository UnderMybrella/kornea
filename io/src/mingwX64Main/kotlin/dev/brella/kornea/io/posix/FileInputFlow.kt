package dev.brella.kornea.io.posix

import kotlinx.cinterop.CPointer
import dev.brella.kornea.io.common.*
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import platform.posix.FILE
import platform.posix.SEEK_CUR
import platform.posix.SEEK_END
import platform.posix.SEEK_SET

class FileInputFlow(val fp: FilePointer, override val location: String? = null): SeekableInputFlow {
    constructor(fp: CPointer<FILE>, location: String? = null): this(
        FilePointer(
            fp
        ), location)

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

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> io { fp.seek(pos, SEEK_SET) }
            EnumSeekMode.FROM_POSITION -> io { fp.seek(pos, SEEK_CUR) }
            EnumSeekMode.FROM_END -> io { fp.seek(pos, SEEK_END) }
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