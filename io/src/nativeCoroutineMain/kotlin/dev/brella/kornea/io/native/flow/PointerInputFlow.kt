package dev.brella.kornea.io.native.flow

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.BufferedInputFlow
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import dev.brella.kornea.io.common.flow.WindowedInputFlow
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get

public class PointerInputFlow(public val pointer: CPointer<ByteVar>, override val location: String? = pointer.toString()): SeekableInputFlow, BaseDataCloseable() {
    private var offset: Int = 0

    override suspend fun read(): Int = pointer[offset++].toInt()
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int {
        repeat(len) { i -> b[off + i] = pointer[offset++] }

        return len
    }

    override suspend fun skip(n: ULong): ULong? {
        offset += n.toInt()
        return n
    }

    override suspend fun position(): ULong = offset.toULong()
    override suspend fun available(): ULong? = null
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> offset = 0
            EnumSeekMode.FROM_END -> throw IllegalStateException("Cannot seek to the 'end' of a pointer flow!")
            EnumSeekMode.FROM_POSITION -> offset += pos.toInt()
        }

        return position()
    }
}

public inline fun BufferedPointerInputFlow(pointer: CPointer<ByteVar>, location: String? = pointer.toString()): SeekableInputFlow =
    BufferedInputFlow.Sink.Seekable(PointerInputFlow(pointer, location))

public suspend inline fun BufferedPointerInputFlow(pointer: CPointer<ByteVar>, length: Int, location: String? = pointer.toString()): SeekableInputFlow =
    BufferedInputFlow.Sink.Seekable(WindowedInputFlow.Seekable(PointerInputFlow(pointer, location), 0uL, length.toULong()))

public suspend inline fun BufferedPointerInputFlow(pointer: CPointer<ByteVar>, offset: ULong, length: ULong, location: String? = pointer.toString()): SeekableInputFlow =
    BufferedInputFlow.Sink.Seekable(WindowedInputFlow.Seekable(PointerInputFlow(pointer, location), offset, length))