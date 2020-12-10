package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.Url
import dev.brella.kornea.toolkit.common.DataCloseableEventHandler

@ExperimentalUnsignedTypes
public open class BufferedOutputFlow(protected val backing: OutputFlow) : BaseDataCloseable(), CountingOutputFlow,
    PrintOutputFlow, OutputFlowState, IntFlowState by IntFlowState.base() {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    protected var _flowCount: Long = 0L
    public val flowCount: Long
        get() = _flowCount

    override val streamOffset: Long
        get() = if (backing is CountingOutputFlow) backing.streamOffset + flowCount else flowCount

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
    protected var bufferCount: Int = 0

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

    override suspend fun whenClosed() {
        super.whenClosed()

        flush()
        backing.close()
    }

    override fun locationAsUrl(): KorneaResult<Url> = backing.locationAsUrl()
}