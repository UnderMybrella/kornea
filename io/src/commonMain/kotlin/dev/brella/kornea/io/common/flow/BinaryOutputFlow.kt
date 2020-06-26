package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.toolkit.common.BinaryListView
import dev.brella.kornea.toolkit.common.BinaryView
import dev.brella.kornea.toolkit.common.asImmutableView
import dev.brella.kornea.toolkit.common.map

@ExperimentalUnsignedTypes
@ChangedSince(KorneaIO.VERSION_4_1_0, "BinaryOutputFlow is now an interface")
@ChangedSince(KorneaIO.VERSION_1_0_0_ALPHA, "Removed getBufferView and implement BinaryView")
public interface BinaryOutputFlow : CountingOutputFlow, BinaryView {
    public companion object {
        public operator fun invoke(): BinaryOutputFlow = ListBacked()
    }

    public open class ListBacked(private val buffer: MutableList<Byte>) :
        BaseDataCloseable(), BinaryOutputFlow, PrintOutputFlow, BinaryView {

        public constructor() : this(ArrayList())

        override val streamOffset: Long
            get() = buffer.size.toLong()

        override suspend fun write(byte: Int) {
            buffer.add(byte.toByte())
        }

        override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
        override suspend fun write(b: ByteArray, off: Int, len: Int) {
            buffer.addAll(b.slice(off until off + len))
        }

        override suspend fun flush() {}

        override fun getData(): ByteArray = buffer.toByteArray()
        override fun getDataSize(): ULong = buffer.size.toULong()

        override suspend fun size(): Int = buffer.size
        override suspend fun get(index: Int): Int = buffer[index].toInt() and 0xFF
        override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int) {
            buffer.subList(start, end).forEachIndexed { index, byte -> dest[destOffset + index] = byte }
        }
    }

    public fun getData(): ByteArray
    public fun getDataSize(): ULong
}