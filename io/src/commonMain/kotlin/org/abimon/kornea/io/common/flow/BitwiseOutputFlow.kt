package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.DataCloseableEventHandler

@ExperimentalUnsignedTypes
open class BitwiseOutputFlow(val flow: OutputFlow): OutputFlow {
    private var currentInt = 0
    private var currentPos = 0
    //    private val builder = StringBuilder()

//    fun Int.toBitHex(): String = toString(16).padStart(2, '0')

    open suspend infix fun write(bool: Boolean) = encodeData { bit(if (bool) 1 else 0) }
    open suspend infix fun writeByte(byte: Number) = byte(byte)
    open suspend infix fun writeShort(short: Number) = short(short)
    open suspend infix fun writeInt(int: Number) = int(int)
    open suspend infix fun writeLong(long: Number) = long(long)
    open suspend infix fun writeFloat(float: Float) = int(float.toBits())

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

    open suspend fun <T> encodeData(needed: Int = 1, op: () -> T): T {
        checkBefore(needed)
        try {
            return op()
        } finally {
            checkAfter()
        }
    }

    open fun bit(bit: Int) {
        currentInt = currentInt or (bit shl currentPos++)
    }

    open suspend fun byte(num: Number) = number(num.toLong(), 8)
    open suspend fun short(num: Number) = number(num.toLong(), 16)
    open suspend fun int(num: Number) = number(num.toLong(), 32)
    open suspend fun long(num: Number) = number(num.toLong(), 64)

    open suspend fun number(num: Long, bits: Int) {
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

    protected suspend fun checkBefore(needed: Int = 1) {
        if (currentPos > (8 - needed) && currentPos > 0) { //hardcoded check just to make sure we don't write a 0 byte
            encodeByte()
            currentInt = 0
            currentPos = 0
        }
    }

    protected suspend fun checkAfter() {
        if (currentPos >= 8) {
            encodeByte()
            currentInt = 0
            currentPos = 0
        }
    }

    protected suspend fun encodeByte() = encode(currentInt)
    protected suspend fun encode(byte: Int) {
//        builder.append(byte.toBitHex())
        flow.write(byte and 0xFF)
    }

    /** Output Flow */
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    override suspend fun write(byte: Int) = writeByte(byte)
    override suspend fun write(b: ByteArray) = write(b, 0, b.size)
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

    override suspend fun close() {
        super.close()

        closed = true
    }
}