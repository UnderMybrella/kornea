package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.DataCloseableEventHandler

@ExperimentalUnsignedTypes
class BufferedOutputFlow(val backing: OutputFlow): CountingOutputFlow {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    var _flowCount = 0L
    val flowCount
        get() = _flowCount

    override val streamOffset: Long
        get() = if (backing is CountingOutputFlow) backing.streamOffset + flowCount else flowCount

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    /**
     * The internal buffer where data is stored.
     */
    protected var buf: ByteArray = ByteArray(8192)

    /**
     * The number of valid bytes in the buffer. This value is always
     * in the range `0` through `buf.length`; elements
     * `buf[0]` through `buf[count-1]` contain valid
     * byte data.
     */
    protected var bufferCount = 0

    private suspend fun flushBuffer() {
        if (bufferCount > 0 && !closed) {
            backing.write(buf, 0, bufferCount)
            bufferCount = 0
        }
    }

    override suspend fun write(byte: Int) {
        if (closed) return

        if (bufferCount >= buf.size) {
            flushBuffer()
        }

        _flowCount++
        buf[bufferCount++] = byte.toByte()
    }

    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        if (closed) {
            return
        }

        _flowCount += len

        if (len >= buf.size) {
            /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
            flushBuffer()
            backing.write(b, off, len)
            return
        }

        if (len > buf.size - bufferCount) {
            flushBuffer()
        }

        b.copyInto(buf, bufferCount, off, off + len)
        bufferCount += len
    }

    override suspend fun flush() {
        flushBuffer()
        backing.flush()
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            flush()
            backing.close()
            closed = true
        }
    }
}