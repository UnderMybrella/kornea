package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.DataCloseableEventHandler

@ExperimentalUnsignedTypes
open class BinaryOutputFlow(val buffer: MutableList<Byte>): CountingOutputFlow {
    constructor(): this(ArrayList())

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    override val streamOffset: Long
        get() = buffer.size.toLong()

    override suspend fun write(byte: Int) {
        buffer.add(byte.toByte())
    }
    override suspend fun write(b: ByteArray) = write(b, 0, b.size)
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        buffer.addAll(b.slice(off until off + len))
    }
    override suspend fun flush() {}
    fun getData(): ByteArray = buffer.toByteArray()
    fun getDataSize(): ULong = buffer.size.toULong()

    override suspend fun close() {
        super.close()

        closed = true
    }
}