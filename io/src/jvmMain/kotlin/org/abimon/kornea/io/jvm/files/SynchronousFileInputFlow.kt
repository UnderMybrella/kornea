package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.SeekableInputFlow
import org.abimon.kornea.io.common.flow.readResultIsValid
import java.io.File
import java.io.RandomAccessFile

@ExperimentalUnsignedTypes
public class SynchronousFileInputFlow(
    public val backingFile: File,
    override val location: String? = backingFile.absolutePath
) : BaseDataCloseable(), InputFlow, SeekableInputFlow {
    private val channel = RandomAccessFile(backingFile, "r")

    override suspend fun read(): Int? = withContext(Dispatchers.IO) { channel.read().takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? =
        withContext(Dispatchers.IO) { channel.read(b, off, len).takeIf(::readResultIsValid) }

    override suspend fun skip(n: ULong): ULong = withContext(Dispatchers.IO) {
        channel.seek(channel.filePointer + n.toLong())
        n
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = withContext(Dispatchers.IO) { channel.length().toULong() }
    override suspend fun position(): ULong = withContext(Dispatchers.IO) { channel.filePointer.toULong() }

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> withContext(Dispatchers.IO) { channel.seek(pos) }
            EnumSeekMode.FROM_POSITION -> withContext(Dispatchers.IO) { channel.seek(channel.filePointer + pos) }
            EnumSeekMode.FROM_END -> withContext(Dispatchers.IO) { channel.seek(channel.length() - pos) }
        }

        return position()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        withContext(Dispatchers.IO) { channel.close() }
    }
}