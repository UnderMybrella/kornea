package dev.brella.kornea.io.posix

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Url
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

    public fun locationAsUrl(): KorneaResult<Url> {
        val fd = fileno(fp)
        if (fd < 0) return KorneaResult.empty()

        val buffer = ByteArray(256)
        val linkLength = readlink("/proc/self/fd/$fd", buffer.refTo(0), buffer.size.toULong())
        if (linkLength <= 0) return KorneaResult.empty()

        val linkPath = buffer.toKString(endIndex = linkLength.toInt())
        return KorneaResult.success(Url.fromFile(linkPath), null)
    }

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

