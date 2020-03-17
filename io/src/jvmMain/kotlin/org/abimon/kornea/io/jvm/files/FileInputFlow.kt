package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.readResultIsValid
import java.io.File
import java.io.RandomAccessFile

@ExperimentalUnsignedTypes
class FileInputFlow(val backingFile: File, override val location: String? = backingFile.absolutePath) : InputFlow {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val channel = RandomAccessFile(backingFile, "r")
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun read(): Int? = withContext(Dispatchers.IO) { channel.read().takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? = withContext(Dispatchers.IO) { channel.read(b, off, len).takeIf(::readResultIsValid) }

    override suspend fun skip(n: ULong): ULong = withContext(Dispatchers.IO) {
        channel.seek(channel.filePointer + n.toLong())
        n
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = withContext(Dispatchers.IO) { channel.length().toULong() }
    override suspend fun position(): ULong = withContext(Dispatchers.IO) { channel.filePointer.toULong() }

    override suspend fun seek(pos: Long, mode: Int): ULong? {
        when (mode) {
            InputFlow.FROM_BEGINNING -> withContext(Dispatchers.IO) { channel.seek(pos) }
            InputFlow.FROM_POSITION -> withContext(Dispatchers.IO) { channel.seek(channel.filePointer + pos) }
            InputFlow.FROM_END -> withContext(Dispatchers.IO) { channel.seek(channel.length() - pos) }
            else -> return null
        }

        return position()
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            withContext(Dispatchers.IO) { channel.close() }
            closed = true
        }
    }
}