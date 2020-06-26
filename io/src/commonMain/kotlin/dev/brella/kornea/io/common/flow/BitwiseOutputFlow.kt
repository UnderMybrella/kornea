package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.BaseDataCloseable

@ExperimentalUnsignedTypes
public open class BitwiseOutputFlow(public val flow: OutputFlow): BaseDataCloseable(), OutputFlow {
    private var currentInt = 0
    private var currentPos = 0
    //    private val builder = StringBuilder()

//    fun Int.toBitHex(): String = toString(16).padStart(2, '0')

    public open suspend infix fun write(bool: Boolean): Unit = encodeData { bit(if (bool) 1 else 0) }
    public open suspend infix fun writeByte(byte: Number): Unit = byte(byte)
    public open suspend infix fun writeShort(short: Number): Unit = short(short)
    public open suspend infix fun writeInt(int: Number): Unit = int(int)
    public open suspend infix fun writeLong(long: Number): Unit = long(long)
    public open suspend infix fun writeFloat(float: Float): Unit = int(float.toBits())

//    open infix fun writeVariableInt(int: Number) {
//        val i = int.toInt()
//
//        this writeByte i
//        if (i > 0x7F) {
//            this writeByte ((i shr 8) and 0xFF)
//
//            if (i > 0x7FFF) {
//                this writeByte ((i shr 16) and 0xFF)
//
//                if (i > 0x7FFFFF) {
//                    this writeByte ((i shr 24) and 0xFF)
//                }
//            }
//        }
//    }

    public suspend inline fun <T> encodeData(needed: Int = 1, op: () -> T): T {
        checkBefore(needed)
        try {
            return op()
        } finally {
            checkAfter()
        }
    }

    public open fun bit(bit: Int) {
        currentInt = currentInt or (bit shl currentPos++)
    }

    public open suspend fun byte(num: Number): Unit = number(num.toLong(), 8)
    public open suspend fun short(num: Number): Unit = number(num.toLong(), 16)
    public open suspend fun int(num: Number): Unit = number(num.toLong(), 32)
    public open suspend fun long(num: Number): Unit = number(num.toLong(), 64)

    public open suspend fun number(num: Long, bits: Int) {
        val availableBits = 8 - currentPos
        checkBefore(availableBits)
        for (i in 0 until availableBits)
            bit(((num shr i) and 1).toInt())
        checkAfter()
        var offset = availableBits
        for (i in 0 until (bits / 8) - 1) {
            encode(((num shr offset) and 0xFF).toInt())
            offset += 8
        }
        checkBefore(8 - availableBits)
        for (i in offset until bits)
            bit(((num shr i) and 1).toInt())
        checkAfter()
    }

    public suspend fun checkBefore(needed: Int = 1) {
        if (currentPos > (8 - needed) && currentPos > 0) { //hardcoded check just to make sure we don't write a 0 byte
            encodeByte()
            currentInt = 0
            currentPos = 0
        }
    }

    public suspend fun checkAfter() {
        if (currentPos >= 8) {
            encodeByte()
            currentInt = 0
            currentPos = 0
        }
    }

    protected suspend fun encodeByte(): Unit = encode(currentInt)
    protected suspend fun encode(byte: Int) {
//        builder.append(byte.toBitHex())
        flow.write(byte and 0xFF)
    }

    override suspend fun write(byte: Int): Unit = writeByte(byte)
    override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        if (currentPos == 0) {
            return flow.write(b, off, len)
        } else {
            for (i in 0 until len) {
                writeByte(b[off + i])
            }
        }
    }
    override suspend fun flush() {}
}