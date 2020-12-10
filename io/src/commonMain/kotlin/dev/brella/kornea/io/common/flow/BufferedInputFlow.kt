package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.IntFlowState.Companion.base
import kotlin.math.min

@ExperimentalUnsignedTypes
@ChangedSince(KorneaIO.VERSION_4_2_0_INDEV)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public abstract class BufferedInputFlow(override val location: String?) : BaseDataCloseable(), PeekableInputFlow, InputFlowState, IntFlowState by base() {
    public companion object {
        public const val DEFAULT_BUFFER_SIZE: Int = 8192
        public const val MAX_BUFFER_SIZE: Int = Int.MAX_VALUE - 8

        public inline operator fun invoke(backing: InputFlow, location: String? = backing.location): Sink =
            Sink(backing, location)

        public inline operator fun invoke(backing: SeekableInputFlow, location: String? = backing.location): Sink.Seekable =
            Sink.Seekable(backing, location)
    }

    public open class Sink(protected open val backing: InputFlow, location: String? = backing.location): BufferedInputFlow(location) {
        public open class Seekable(public override val backing: SeekableInputFlow, location: String? = backing.location): Sink(backing, location), SeekableInputFlow {
            override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
                val shift = backing.seek(pos, mode)
                fill()
                return shift
            }
        }

//        override suspend fun fillImpl(): Int? = readImpl(buffer, pos, buffer.size - pos)
        override suspend fun readImpl(b: ByteArray, off: Int, len: Int): Int? = backing.read(b, off, len)

        override suspend fun available(): ULong = (count - pos).toULong() + (backing.available() ?: 0uL)
        override suspend fun position(): ULong = backing.position() + (pos - count).toULong()
        override suspend fun remaining(): ULong = available()
        override suspend fun size(): ULong? = backing.size()

        override suspend fun whenClosed() {
            super.whenClosed()
            backing.close()
        }

        override fun locationAsUri(): KorneaResult<Uri> = backing.locationAsUri()
    }

    protected var buffer: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)
    protected var count: Int = 0
    protected var pos: Int = 0
    protected var absPos: Long = 0L

    protected suspend fun fill() {
        pos = 0
        fillPartial()
    }

    protected suspend fun fillPartial() {
        count = 0
        val n = fillImpl()
        if (n ?: 0 > 0) {
            count = n!! + pos
            absPos += n
        }
    }

    protected open suspend fun fillImpl(): Int? = readImpl(buffer, pos, buffer.size - pos)
    protected abstract suspend fun readImpl(b: ByteArray, off: Int, len: Int): Int?

    override suspend fun peek(forward: Int): Int? {
        if (pos >= count) {
            fill()
            if (pos >= count) {
                return null
            }
        } else if (pos + forward > count) {
            if ((pos + forward + 1) - count < buffer.size) { /* Shuffle down */
                buffer.copyOfRange(pos, count).copyInto(buffer, 0, 0, count - pos)
                pos = count - pos
                fillPartial()
                pos = 0
            } else if (buffer.size >= MAX_BUFFER_SIZE) {
                throw IllegalStateException("OOM; Required array size too large")
            } else { /* Grow */
                /* grow buffer */
                val nbuf = ByteArray(if (pos <= MAX_BUFFER_SIZE - pos) pos * 2 else MAX_BUFFER_SIZE)
                buffer.copyInto(nbuf, 0, 0, pos)
                buffer = nbuf
                fillPartial()
            }
        }

        return buffer[pos + forward - 1].toInt() and 0xFF
    }

    override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? {
        if (pos >= count) {
            fill()
            if (pos >= count) {
                return null
            }
        } else if (pos + forward > count) {
            if ((pos + forward) - count < buffer.size) { /* Shuffle down */
                buffer.copyOfRange(pos, count).copyInto(buffer, 0, 0, count - pos)
                pos = count - pos
                fillPartial()
                pos = 0
            } else if (buffer.size >= MAX_BUFFER_SIZE) {
                throw IllegalStateException("OOM; Required array size too large")
            } else { /* Grow */
                /* grow buffer */
                val nbuf = ByteArray(if (pos <= MAX_BUFFER_SIZE - pos) pos * 2 else MAX_BUFFER_SIZE)
                buffer.copyInto(nbuf, 0, 0, pos)
                buffer = nbuf
                fillPartial()
            }
        }

        val peeking = min(len, count - (pos + forward - 1))
        buffer.copyInto(b, destinationOffset = off, pos + forward - 1, pos + forward - 1 + peeking)
        return peeking
    }

    override suspend fun read(): Int? {
        if (pos >= count) {
            fill()
            if (pos >= count) {
                return null
            }
        }

        return buffer[pos++].toInt() and 0xFF
    }

    protected suspend fun read1(b: ByteArray, off: Int, len: Int): Int? {
        var avail = count - pos
        if (avail <= 0) {
            if (len >= buffer.size) {
                return readImpl(b, off, len)
            }

            fill()

            avail = count - pos
            if (avail <= 0) return -1
        }

        val cnt = if (avail < len) avail else len
        buffer.copyInto(b, off, pos, pos + cnt)
        pos += cnt
        return cnt
    }

    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if ((off or len or (off + len) or (b.size - (off + len))) < 0) {
            throw IndexOutOfBoundsException()
        } else if (len == 0) {
            return 0
        }

        var n = 0

        while (true) {
            val nread = read1(b, off + n, len - n) ?: 0
            if (nread <= 0)
                return if (n == 0) nread else n
            n += nread
            if (n >= len)
                return n
            if (available() <= 0u)
                return n
        }
    }

    override suspend fun skip(n: ULong): ULong? {
        val avail = count - pos
        if (avail <= 0) {
            return null
        }

        if (avail < n.toInt()) {
            pos += avail

            return avail.toULong() + (skip(n - avail.toULong()) ?: 0uL)
        } else {
            pos += n.toInt()

            return n
        }
    }

    override suspend fun available(): ULong = (count - pos).toULong()
    override suspend fun position(): ULong = (absPos - count + pos).toULong()
}