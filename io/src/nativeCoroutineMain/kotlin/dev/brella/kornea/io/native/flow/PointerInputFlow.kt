package dev.brella.kornea.io.native.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.*
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class PointerInputFlow(public val pointer: CPointer<ByteVar>, override val location: String? = pointer.toString()): SeekableInputFlow, BaseDataCloseable(), InputFlowState, IntFlowState by IntFlowState.base() {
    private var offset: Int = 0

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    override suspend fun read(): Int = pointer[offset++].toInt()
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int {
        repeat(len) { i -> b[off + i] = pointer[offset++] }

        return len
    }

    override suspend fun skip(n: ULong): ULong {
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