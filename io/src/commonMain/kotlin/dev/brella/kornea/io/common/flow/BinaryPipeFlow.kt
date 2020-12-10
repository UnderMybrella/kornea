package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.*
import kotlin.math.min

@ExperimentalUnsignedTypes
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface BinaryPipeFlow
    : SeekablePipeFlow<BinaryPipeFlow, BinaryPipeFlow>,
    PeekableInputFlow, SeekableInputFlow,
    CountingOutputFlow, SeekableOutputFlow {
    public companion object {
        public operator fun invoke(): BinaryPipeFlow = ListBacked()
    }

    public open class ListBacked(
        private val view: MutableList<Byte>,
        private var pos: Int = 0,
        override val location: String? = null
    ) : BaseDataCloseable(), BinaryPipeFlow, PrintOutputFlow {
        public constructor() : this(ArrayList())

        override val input: BinaryPipeFlow
            get() = this
        override val output: BinaryPipeFlow
            get() = this

        override val streamOffset: Long
            get() = pos.toLong()

        override suspend fun peek(forward: Int): Int? =
            if ((pos + forward - 1) < view.size) view[pos + forward - 1].toInt().and(0xFF) else null

        override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? {
            if (pos + forward - 1 < view.size) {
                val peeking = min(len, view.size - (pos + forward - 1))
                view.subList(pos + forward - 1, pos + forward - 1 + peeking).forEachIndexed { index, byte -> b[off + index] = byte }
                return peeking
            }

            return null
        }

        override suspend fun read(): Int? = if (pos < view.size) view[pos++].toInt().and(0xFF) else null
        override suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
        override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
            if (len < 0 || off < 0 || len > b.size - off)
                throw IndexOutOfBoundsException()

            if (pos >= view.size)
                return null

            val avail = view.size - pos

            @Suppress("NAME_SHADOWING")
            val len: Int = if (len > avail) avail else len
            if (len <= 0)
                return 0

            view.subList(pos , pos + len).forEachIndexed { index, byte -> b[off + index] = byte }
            pos += len
            return len
        }

        override suspend fun skip(n: ULong): ULong? {
            val k = min((view.size - pos).toULong(), n)
            pos += k.toInt()
            return k
        }

        override suspend fun available(): ULong = remaining()
        override suspend fun remaining(): ULong = (view.size - pos).toULong()
        override suspend fun size(): ULong = view.size.toULong()
        override suspend fun position(): ULong = pos.toULong()

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> this.pos = pos.toInt()
                EnumSeekMode.FROM_POSITION -> this.pos += pos.toInt()
                EnumSeekMode.FROM_END -> this.pos = view.size - pos.toInt()
            }

            return position()
        }

        override suspend fun write(byte: Int) {
            view.add(pos++, byte.toByte())
        }

        override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
        override suspend fun write(b: ByteArray, off: Int, len: Int) {
            view.addAll(pos, b.slice(off until off + len))
            pos += len.coerceAtMost(b.size - off)
        }

        override suspend fun flush() {}

        override fun getData(): ByteArray = view.toByteArray()
        override fun getDataSize(): ULong = view.size.toULong()
    }

    public fun getData(): ByteArray
    public fun getDataSize(): ULong

    override fun locationAsUrl(): KorneaResult<Url> = KorneaResult.empty()
}