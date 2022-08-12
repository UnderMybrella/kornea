package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.toolkit.common.BinaryView
import kotlin.math.min

@ChangedSince(KorneaIO.VERSION_4_1_0_INDEV, "BinaryOutputFlow is now an interface")
@ChangedSince(KorneaIO.VERSION_1_0_0_ALPHA, "Removed getBufferView and implement BinaryView")
public interface BinaryOutputFlow : SeekableFlow, OutputFlow, BinaryView {
    public companion object {
        public operator fun invoke(location: String? = null): BinaryOutputFlow = ListBacked(location)
    }

    public open class ListBacked(private val buffer: MutableList<Byte>, override val location: String?) :
        BaseDataCloseable(), BinaryOutputFlow, BinaryView {
        private var position: Int = -1

        public constructor(location: String?) : this(ArrayList(), location)

        override suspend fun write(byte: Int) {
            if (position < 0) {
                buffer.add(byte.toByte())
            } else if (position < buffer.size) {
                buffer[position++] = byte.toByte()
            } else {
                position = -1
                buffer.add(byte.toByte())
            }
        }

        override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
        override suspend fun write(b: ByteArray, off: Int, len: Int) {
            if (position < 0) {
                buffer.addAll(b.slice(off until off + len))
            } else {
                val space = buffer.size - position
                for (i in off until off + min(space, len)) {
                    buffer[position++] = b[off + i]
                }

                if (space < len) {
                    buffer.addAll(b.slice((off + space) until (off + len)))
                    position = -1
                }
            }
        }

        override suspend fun flush() {}
        override suspend fun position(): ULong = if (position < 0) buffer.size.toULong() else position.toULong()

        override fun getData(): ByteArray = buffer.toByteArray()
        override fun getDataSize(): ULong = buffer.size.toULong()
        override suspend fun size(): Int = buffer.size
        override suspend fun get(index: Int): Int = buffer[index].toInt() and 0xFF
        override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int) {
            buffer.subList(start, end).forEachIndexed { index, byte -> dest[destOffset + index] = byte }
        }

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            val seekPos = when (mode) {
                EnumSeekMode.FROM_BEGINNING -> pos.toInt()
                EnumSeekMode.FROM_END -> buffer.size - pos.toInt() - 1
                EnumSeekMode.FROM_POSITION -> this.position + pos.toInt()
            }

            if (seekPos > buffer.size) {
                buffer.addAll(ByteArray(pos.toInt() - buffer.size).asList())
                this.position = -1
            } else {
                this.position = pos.toInt().coerceAtLeast(0)
            }

            return position()
        }
    }

    public fun getData(): ByteArray
    public fun getDataSize(): ULong

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()
}

public suspend inline fun buildBinaryFlowData(block: BinaryOutputFlow.() -> Unit): ByteArray =
    BinaryOutputFlow().apply(block).getData()