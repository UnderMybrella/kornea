@file:Suppress("NOTHING_TO_INLINE")

package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.FlowPacket
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.toolkit.common.PrintFlow

public typealias OutputFlowEventHandler = suspend (flow: OutputFlow) -> Unit

public interface OutputFlow: KorneaFlow, PrintFlow {
    public override val location: String?
    public override suspend fun position(): ULong
    public override fun locationAsUri(): KorneaResult<Uri>

    public suspend fun write(byte: Int)
    public suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    public suspend fun write(b: ByteArray, off: Int, len: Int)
    public suspend fun flush()

    override suspend fun print(value: Char): OutputFlow {
        write(value.code)
        return this
    }

    override suspend fun print(value: CharSequence?): OutputFlow {
        write(value.toString().encodeToByteArray())
        return this
    }

    override suspend fun print(value: CharSequence?, startIndex: Int, endIndex: Int): OutputFlow {
        write((value?.subSequence(startIndex, endIndex).toString()).encodeToByteArray())
        return this
    }
}

public interface OutputFlowConstituent: KorneaFlowConstituent {
    override val flow: OutputFlow
}

public interface OutputFlowByDelegate<O: OutputFlow>: OutputFlow {
    public companion object {
        public inline operator fun <O: OutputFlow> invoke(output: O): OutputFlowByDelegate<O> = OutputFlowByDelegateImpl(output)
    }

    public val output: O
}

@PublishedApi
internal class OutputFlowByDelegateImpl<O: OutputFlow>(override val output: O): OutputFlowByDelegate<O>, OutputFlow by output

@Deprecated("Stream offset is now part of KorneaFlow", replaceWith = ReplaceWith("OutputFlow"))
public interface CountingOutputFlow: OutputFlow {
    public val streamOffset: Long
}

@Deprecated("Replace with SeekableFlow", replaceWith = ReplaceWith("SeekableFlow"), level = DeprecationLevel.ERROR)
public interface SeekableOutputFlow: OutputFlow {
    public suspend fun seek(pos: Long, mode: EnumSeekMode): ULong
}

public suspend fun OutputFlow.writeByte(byte: Number): Unit = write(byte.toInt())

public suspend inline fun OutputFlow.writePacket(packet: FlowPacket): Unit = write(packet.buffer, 0, packet.size)//if (read(packet.buffer) == packet.size) packet.buffer else null