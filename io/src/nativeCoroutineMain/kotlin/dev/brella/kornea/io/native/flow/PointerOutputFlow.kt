package dev.brella.kornea.io.native.flow

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.BufferedOutputFlow
import dev.brella.kornea.io.common.flow.SeekableOutputFlow
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.set

public class PointerOutputFlow(public val pointer: CPointer<ByteVar>): SeekableOutputFlow, BaseDataCloseable() {
    public var offset: Int = 0
        private set

    override suspend fun write(byte: Int) {
        pointer[offset++] = byte.toByte()
    }

    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        repeat(len) { i -> pointer[offset++] = b[offset + i] }
    }

    override suspend fun flush() {}

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> offset = 0
            EnumSeekMode.FROM_END -> throw IllegalStateException("Cannot seek to the 'end' of a pointer flow!")
            EnumSeekMode.FROM_POSITION -> offset += pos.toInt()
        }

        return offset.toULong()
    }
}

public inline fun BufferedPointerOutputFlow(pointer: CPointer<ByteVar>): BufferedOutputFlow =
    BufferedOutputFlow(PointerOutputFlow(pointer))