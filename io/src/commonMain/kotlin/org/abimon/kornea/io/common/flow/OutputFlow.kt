@file:Suppress("NOTHING_TO_INLINE")

package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.ObservableDataCloseable

@ExperimentalUnsignedTypes
typealias OutputFlowEventHandler = suspend (flow: OutputFlow) -> Unit

@ExperimentalUnsignedTypes
interface OutputFlow: ObservableDataCloseable {
    suspend fun write(byte: Int)
    suspend fun write(b: ByteArray) = write(b, 0, b.size)
    suspend fun write(b: ByteArray, off: Int, len: Int)
    suspend fun flush()
}

@ExperimentalUnsignedTypes
interface CountingOutputFlow: OutputFlow {
    val streamOffset: Long
}

@ExperimentalUnsignedTypes
open class SinkCountingOutputFlow(val sink: OutputFlow) : CountingOutputFlow, OutputFlow by sink {
    var _count = 0L
    val count
        get() = _count

    override val streamOffset: Long
        get() = if (sink is CountingOutputFlow) sink.streamOffset else count

    override suspend fun write(byte: Int) {
        sink.write(byte)
        _count++
    }

    override suspend fun write(b: ByteArray) {
        sink.write(b)
        _count += b.size
    }

    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        require(len >= 0)
        sink.write(b, off, len)
        _count += len
    }
}

@ExperimentalUnsignedTypes
suspend fun OutputFlow.writeByte(byte: Number) = write(byte.toInt())