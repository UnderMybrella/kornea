package org.abimon.kornea.io.posix

import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.DataCloseable
import org.abimon.kornea.io.common.DataCloseableEventHandler
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
    fun seek(off: Long, whence: Int) = fseek(fp, off, whence)

    @ExperimentalUnsignedTypes
    fun read() = memScoped {
        val char = alloc<UByteVar>()
        return@memScoped char.value.takeIf { fread(char.ptr, 1, 1, fp).toInt() == 1 }?.toInt()
    }

    fun read(buffer: ByteArray, offset: Int, length: Int) =
        buffer.usePinned { pinned -> fread(pinned.addressOf(offset), 1, length.toULong(), fp) }

    fun write(byte: Int) {
        memScoped {
            val char = alloc<UByteVar>()
            char.value = byte.toUByte()
            fwrite(char.ptr, 1, 1, fp)
        }
    }

    fun write(buffer: ByteArray, offset: Int, length: Int) =
        buffer.usePinned { pinned -> fwrite(pinned.addressOf(offset), 1, length.toULong(), fp) }

    fun flush() = fflush(fp)

    override suspend fun close() { fclose(fp) }
}

fun openFile(path: String, modes: String): FilePointer? = fopen(path, modes)?.let(::FilePointer)