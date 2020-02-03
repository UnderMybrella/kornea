package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.DataCloseableEventHandler
import kotlin.math.min

@ExperimentalUnsignedTypes
open class BitwiseInputFlow protected constructor(protected val flow: InputFlow) : InputFlow {
    companion object {
        fun subflow(flow: InputFlow): BitwiseInputFlow = BitwiseInputFlow(flow)
        operator fun invoke(flow: InputFlow): BitwiseInputFlow = if (flow is BitwiseInputFlow) flow else BitwiseInputFlow(flow)
    }

    constructor(str: String) : this(BinaryInputFlow(str.chunked(2).map { it.toInt(16).toByte() }.toByteArray()))
    constructor(data: ByteArray) : this(BinaryInputFlow(data))

    var currentInt: Int? = null
    var currentPos = 0

    suspend fun skipBits(bits: Int) {
        if (bits == 0) return
        require(bits > 0)

        if (bits >= 8 && bits % 8 == 0) {
            flow.skip((bits / 8).toULong())
        } else {
            if (8 - currentPos > bits) {
                currentPos += bits
            } else {
                val bitsRemain = bits - (8 - currentPos)
                val bytesToSkip = bitsRemain / 8
                if (bytesToSkip > 0)
                    flow.skip(bytesToSkip.toULong())
                currentPos = bitsRemain % 8
            }
        }
    }

    suspend fun readBoolean(): Boolean? = readBit()?.equals(1)
    suspend fun readBit(): Int? = decodeData { bit() }
    suspend fun readByte(): Byte? = readNumber(8)?.toByte()
    suspend fun readShort(): Short? = readNumber(16)?.toShort()
    suspend fun readInt(): Int? = readNumber(32)?.toInt()
    suspend fun readLong(): Long? = readNumber(64)
    suspend fun readFloat(): Float? = readInt()?.let(Float.Companion::fromBits)

    suspend fun readNumber(bits: Int): Long? {
        var result = 0L

        //Read first x bits
        val availableBits = min(8 - currentPos, bits)
        checkBefore(availableBits)
        for (i in 0 until availableBits)
            result = result or ((bit() ?: return null).toLong() shl i)

        var offset = availableBits
        for (i in 0 until (bits / 8) - 1) {
            result = result or ((flow.read() ?: return null).toLong() shl offset)
            offset += 8
        }

        checkAfter() //This goes after the read calls so that we ensure we don't skip a byte

        //Read last x bits
        checkBefore(bits - offset)
        for (i in offset until bits)
            result = result or ((bit() ?: return null).toLong() shl i)
        checkAfter()

        return result
    }

    open suspend fun <T> decodeData(needed: Int = 1, op: () -> T): T {
        checkBefore(needed)
        try {
            return op()
        } finally {
            checkAfter()
        }
    }

    fun bit(): Int? = currentInt?.and(0xFF)?.shr(currentPos++)?.and(1)

    suspend fun checkBefore(needed: Int = 1) {
        if (currentInt == null || currentPos > (8 - needed)) { //hardcoded check just to make sure we don't write a 0 byte
            currentInt = flow.read()
            currentPos = 0
        }
    }

    suspend fun checkAfter() {
        if (currentInt == null || currentPos >= 8) {
            currentInt = flow.read()
            currentPos = 0
        }
    }

    /** InputFlow */
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun read(): Int? = readByte()?.toInt()?.and(0xFF)
    override suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        if (currentPos == 0) {
            checkBefore(8)
            b[off] = currentInt?.toByte() ?: return null
            val result = flow.read(b, off + 1, len - 1)?.plus(1) ?: 1
            currentInt = flow.read()
            return result
        } else {
            checkBefore(8)
            b[off] = readByte() ?: return null

            for (i in 1 until len) {
                b[off + i] = readByte() ?: return i
            }

            return len
        }
    }

    override suspend fun skip(n: ULong): ULong? {
        if (n == 0uL) return 0u
        val skip = flow.skip(n.toLong() - 1)
        currentInt = flow.read()
        return skip
    }

    override suspend fun available(): ULong? = remaining()
    override suspend fun remaining(): ULong? = flow.remaining()
    override suspend fun size(): ULong? = flow.size()
    override suspend fun position(): ULong = flow.position()

    override suspend fun seek(pos: Long, mode: Int): ULong? {
        when (mode) {
            InputFlow.FROM_BEGINNING -> if (pos >= flow.position().toLong()) skip(pos.toULong() - flow.position()) else return null
            InputFlow.FROM_POSITION -> if (pos >= 0) skip(pos) else return null
            InputFlow.FROM_END ->
                if ((size()?.toLong()?.minus(pos) ?: 0L) >= position().toLong())
                    skip(size()?.toLong()?.minus(pos)?.minus(position().toLong()) ?: 0)
                else
                    return null
            else -> return null
        }

        return position()
    }

    override suspend fun close() {
        super.close()
        closed = true
    }
}