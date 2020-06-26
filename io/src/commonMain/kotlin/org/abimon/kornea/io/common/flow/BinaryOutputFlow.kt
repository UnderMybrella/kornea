package org.abimon.kornea.io.common.flow

import org.abimon.kornea.annotations.ChangedSince
import org.abimon.kornea.io.common.BaseDataCloseable
import org.abimon.kornea.io.common.KorneaIO
import org.kornea.toolkit.common.asImmutableView

@ExperimentalUnsignedTypes
@ChangedSince(KorneaIO.VERSION_4_1_0, "BinaryOutputFlow is now an interface")
public interface BinaryOutputFlow: CountingOutputFlow {
    public companion object {
        public operator fun invoke(): BinaryOutputFlow = ListBacked()
    }

    public open class ListBacked(private val buffer: MutableList<Byte>): BaseDataCloseable(), BinaryOutputFlow, PrintOutputFlow {
        public constructor(): this(ArrayList())

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
        override fun getBufferView(): List<Byte> = buffer.asImmutableView()
    }

    public fun getData(): ByteArray
    public fun getDataSize(): ULong
    public fun getBufferView(): List<Byte>
}