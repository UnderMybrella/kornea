@file:Suppress("NOTHING_TO_INLINE")

package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.FlowPacket
import dev.brella.kornea.toolkit.common.ObservableDataCloseable

@ExperimentalUnsignedTypes
public typealias OutputFlowEventHandler = suspend (flow: OutputFlow) -> Unit

@ExperimentalUnsignedTypes
public interface OutputFlow: ObservableDataCloseable {
    public suspend fun write(byte: Int)
    public suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    public suspend fun write(b: ByteArray, off: Int, len: Int)
    public suspend fun flush()
}

public interface OutputFlowByDelegate<O: OutputFlow>: OutputFlow {
    public val output: O
}

@ExperimentalUnsignedTypes
public interface CountingOutputFlow: OutputFlow {
    public val streamOffset: Long
}

@ExperimentalUnsignedTypes
public interface SeekableOutputFlow: OutputFlow {

}

@ExperimentalUnsignedTypes
public open class SinkCountingOutputFlow(protected val sink: OutputFlow) : CountingOutputFlow, OutputFlow by sink {
    private var _count: Long = 0L
    public val count: Long
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
public suspend fun OutputFlow.writeByte(byte: Number): Unit = write(byte.toInt())

@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writePacket(packet: FlowPacket): Unit = write(packet.buffer)//if (read(packet.buffer) == packet.size) packet.buffer else null