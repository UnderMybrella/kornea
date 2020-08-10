package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.*
import dev.brella.kornea.toolkit.common.BinaryArrayView
import dev.brella.kornea.toolkit.common.BinaryListView
import dev.brella.kornea.toolkit.common.BinaryView
import kotlin.math.min

@ExperimentalUnsignedTypes
public class BinaryInputFlow(
    private val view: BinaryView,
    private var pos: Int = 0,
    override val location: String? = null
) : BaseDataCloseable(), InputFlow, PeekableInputFlow, SeekableInputFlow, IntFlowState, InputFlowState<BinaryInputFlow> {
    private val buffer: ByteArray = ByteArray(8)

    override val int16Packet: Int16Packet
        get() = Int16Packet(buffer)
    override val int24Packet: Int24Packet
        get() = Int24Packet(buffer)
    override val int32Packet: Int32Packet
        get() = Int32Packet(buffer)
    override val int40Packet: Int40Packet
        get() = Int40Packet(buffer)
    override val int48Packet: Int48Packet
        get() = Int48Packet(buffer)
    override val int56Packet: Int56Packet
        get() = Int56Packet(buffer)
    override val int64Packet: Int64Packet
        get() = Int64Packet(buffer)

    override val flow: BinaryInputFlow
        get() = this

    public constructor(array: ByteArray, pos: Int = 0, location: String? = null) :
            this(BinaryArrayView(array), pos, location)

    public constructor(list: List<Byte>, pos: Int = 0, location: String? = null) :
            this(BinaryListView(list), pos, location)

    override suspend fun peek(forward: Int): Int? =
        if ((pos + forward - 1) < view.size()) view.get(pos + forward - 1) else null

    override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? {
        if (pos + forward - 1 < view.size()) {
            val peeking = min(len, view.size() - (pos + forward - 1))
            view.copyInto(b, off, pos + forward - 1, pos + forward - 1 + peeking)
            return peeking
        }

        return null
    }

    override suspend fun read(): Int? = if (pos < view.size()) view.get(pos++) else null
    override suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        if (pos >= view.size())
            return null

        val avail = view.size() - pos

        @Suppress("NAME_SHADOWING")
        val len: Int = if (len > avail) avail else len
        if (len <= 0)
            return 0

        view.copyInto(b, off, pos, pos + len)
        pos += len
        return len
    }

    override suspend fun skip(n: ULong): ULong? {
        val k = min((view.size() - pos).toULong(), n)
        pos += k.toInt()
        return k
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = (view.size() - pos).toULong()
    override suspend fun size(): ULong = view.size().toULong()
    override suspend fun position(): ULong = pos.toULong()

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> this.pos = pos.toInt()
            EnumSeekMode.FROM_POSITION -> this.pos += pos.toInt()
            EnumSeekMode.FROM_END -> this.pos = view.size() - pos.toInt()
        }

        return position()
    }
}