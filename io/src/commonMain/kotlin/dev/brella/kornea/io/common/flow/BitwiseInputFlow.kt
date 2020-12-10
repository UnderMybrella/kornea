package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.*
import kotlin.math.min

@ExperimentalUnsignedTypes
public open class BitwiseInputFlow protected constructor(protected val flow: InputFlow, override val location: String? = null) : BaseDataCloseable(), InputFlow {
    public companion object {
        public fun subflow(flow: InputFlow, location: String? = flow.location): BitwiseInputFlow = BitwiseInputFlow(flow, location)
        public operator fun invoke(flow: InputFlow, location: String? = flow.location): BitwiseInputFlow = if (flow is BitwiseInputFlow) flow else BitwiseInputFlow(flow, location)
    }

    public constructor(str: String) : this(BinaryInputFlow(str.chunked(2).map { it.toInt(16).toByte() }.toByteArray()))
    public constructor(data: ByteArray) : this(BinaryInputFlow(data))

    private var currentInt: Int? = null
    public var currentPos: Int = 0

    public suspend fun skipBits(bits: Int) {
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

    public suspend fun readBoolean(): Boolean? = readBit()?.equals(1)
    public suspend fun readBit(): Int? = decodeData { bit() }
    public suspend fun readByte(): Byte? = readNumber(8)?.toByte()
    public suspend fun readShort(): Short? = readNumber(16)?.toShort()
    public suspend fun readInt(): Int? = readNumber(32)?.toInt()
    public suspend fun readLong(): Long? = readNumber(64)
    public suspend fun readFloat(): Float? = readInt()?.let(Float.Companion::fromBits)

    public suspend fun readNumber(bits: Int): Long? {
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

    public suspend inline fun <T> decodeData(needed: Int = 1, op: () -> T): T {
        checkBefore(needed)
        try {
            return op()
        } finally {
            checkAfter()
        }
    }

    public fun bit(): Int? = currentInt?.and(0xFF)?.shr(currentPos++)?.and(1)

    public suspend fun checkBefore(needed: Int = 1) {
        if (currentInt == null || currentPos > (8 - needed)) { //hardcoded check just to make sure we don't write a 0 byte
            currentInt = flow.read()
            currentPos = 0
        }
    }

    public suspend fun checkAfter() {
        if (currentInt == null || currentPos >= 8) {
            currentInt = flow.read()
            currentPos = 0
        }
    }

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

    override fun locationAsUrl(): KorneaResult<Url> = KorneaResult.empty()
}