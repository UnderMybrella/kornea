package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.DataCloseableEventHandler
import kotlin.math.min

@ExperimentalUnsignedTypes
class BinaryInputFlow(private val array: ByteArray, private var pos: Int = 0, private var size: Int = array.size,
                      override val location: String? = null):
    PeekableInputFlow {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun peek(forward: Int): Int? = if ((pos + forward - 1) < size) array[pos + forward - 1].toInt() and 0xFF else null
    override suspend fun read(): Int? = if (pos < size) array[pos++].toInt() and 0xFF else null
    override suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        if (pos >= size)
            return null

        val avail = size - pos
        @Suppress("NAME_SHADOWING")
        val len: Int = if (len > avail) avail else len
        if (len <= 0)
            return 0

        array.copyInto(b, off, pos, pos + len)
        pos += len
        return len
    }

    override suspend fun skip(n: ULong): ULong? {
        val k = min((size - pos).toULong(), n)
        pos += k.toInt()
        return k
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = (size - pos).toULong()
    override suspend fun size(): ULong = size.toULong()
    override suspend fun position(): ULong = pos.toULong()

    override suspend fun seek(pos: Long, mode: Int): ULong? {
        when (mode) {
            InputFlow.FROM_BEGINNING -> this.pos = pos.toInt()
            InputFlow.FROM_POSITION -> this.pos += pos.toInt()
            InputFlow.FROM_END -> this.pos = size - pos.toInt()
            else -> return null
        }

        return position()
    }

    override suspend fun close() {
        super.close()
        closed = true
    }
}