package dev.brella.kornea.io.posix

import kotlinx.cinterop.*
import dev.brella.kornea.toolkit.common.DataCloseable
import platform.posix.*

public inline class FilePointer(public val fp: CPointer<FILE>): DataCloseable {
    override val isClosed: Boolean
        get() = fp[0]._fileno == -1

    public fun size(): Long {
        val pos = pos()
        seek(0, SEEK_END)
        val size = pos()
        seek(pos, SEEK_SET)
        return size
    }

    public fun pos(): Long = ftell(fp)
    public fun seek(off: Long, whence: Int): Int = fseek(fp, off, whence)

    @ExperimentalUnsignedTypes
    public fun read(): Int? = fgetc(fp).takeUnless(::isEOF)

    public fun read(buffer: ByteArray, offset: Int, length: Int): ULong =
        buffer.usePinned { pinned -> fread(pinned.addressOf(offset), 1, length.toULong(), fp) }

    public fun write(byte: Int) {
        fputc(byte, fp)
    }

    public fun write(buffer: ByteArray, offset: Int, length: Int): ULong =
        buffer.usePinned { pinned -> fwrite(pinned.addressOf(offset), 1, length.toULong(), fp) }

    public fun flush(): Int = fflush(fp)

    override suspend fun close() {
        fclose(fp)
        fp[0]._fileno = -1
    }
}

public fun openFile(path: String, modes: String): FilePointer? = fopen(path, modes)?.let(::FilePointer)
public fun isEOF(byte: Int): Boolean = byte == EOF