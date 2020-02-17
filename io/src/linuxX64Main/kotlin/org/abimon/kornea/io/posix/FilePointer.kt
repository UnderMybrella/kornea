package org.abimon.kornea.io.posix

import kotlinx.cinterop.*
import org.abimon.kornea.io.common.DataCloseable
import org.abimon.kornea.io.common.flow.readResultIsValid
import platform.posix.*

inline class FilePointer(val fp: CPointer<FILE>): DataCloseable {
    fun size(): Long {
        val pos = pos()
        seek(0, SEEK_END)
        val size = pos()
        seek(pos, SEEK_SET)
        return size
    }

    fun pos(): Long = ftell(fp)
    fun seek(off: Long, whence: Int): Int = fseek(fp, off, whence)

    @ExperimentalUnsignedTypes
    fun read() = fgetc(fp).takeUnless(::isEOF)

    fun read(buffer: ByteArray, offset: Int, length: Int) =
        buffer.usePinned { pinned -> fread(pinned.addressOf(offset), 1, length.toULong(), fp) }

    fun write(byte: Int) {
        fputc(byte, fp)
    }

    fun write(buffer: ByteArray, offset: Int, length: Int) =
        buffer.usePinned { pinned -> fwrite(pinned.addressOf(offset), 1, length.toULong(), fp) }

    fun flush() = fflush(fp)

    override suspend fun close() { fclose(fp) }
}

fun openFile(path: String, modes: String): FilePointer? = fopen(path, modes)?.let(::FilePointer)
fun isEOF(byte: Int): Boolean = byte == EOF