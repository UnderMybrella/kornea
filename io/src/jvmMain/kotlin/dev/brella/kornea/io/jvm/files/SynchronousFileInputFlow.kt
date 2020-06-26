package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import dev.brella.kornea.io.common.flow.readResultIsValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.File
import java.io.RandomAccessFile

@ExperimentalUnsignedTypes
public class SynchronousFileInputFlow(
    public val backingFile: File,
    override val location: String? = backingFile.absolutePath
) : BaseDataCloseable(), InputFlow, SeekableInputFlow {
    private val channel = RandomAccessFile(backingFile, "r")

    override suspend fun read(): Int? = runInterruptible(Dispatchers.IO) { channel.read().takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? =
        runInterruptible(Dispatchers.IO) { channel.read(b, off, len).takeIf(::readResultIsValid) }

    override suspend fun skip(n: ULong): ULong = runInterruptible(Dispatchers.IO) {
        channel.seek(channel.filePointer + n.toLong())
        n
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = runInterruptible(Dispatchers.IO) { channel.length().toULong() }
    override suspend fun position(): ULong = runInterruptible(Dispatchers.IO) { channel.filePointer.toULong() }

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> runInterruptible(Dispatchers.IO) { channel.seek(pos) }
            EnumSeekMode.FROM_POSITION -> runInterruptible(Dispatchers.IO) { channel.seek(channel.filePointer + pos) }
            EnumSeekMode.FROM_END -> runInterruptible(Dispatchers.IO) { channel.seek(channel.length() - pos) }
        }

        return position()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        runInterruptible(Dispatchers.IO) { channel.close() }
    }
}