package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.toolkit.common.*
import kotlin.experimental.and
import kotlin.experimental.or

/** Read from base */

public fun ByteArray.readInt64LE(): Long? {
    if (size < 8)
        return null

    return this[7].asLong(56) or
            this[6].asLong(48) or
            this[5].asLong(40) or
            this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun ByteArray.readInt64BE(): Long? {
    if (size < 8)
        return null

    return this[0].asLong(56) or
            this[1].asLong(48) or
            this[2].asLong(40) or
            this[3].asLong(32) or
            this[4].asLong(24) or
            this[5].asLong(16) or
            this[6].asLong(8) or
            this[7].asLong(0)
}

public fun ByteArray.readUInt64LE(): ULong? {
    if (size < 8)
        return null

    return this[7].asULong(56) or
            this[6].asULong(48) or
            this[5].asULong(40) or
            this[4].asULong(32) or
            this[3].asULong(24) or
            this[2].asULong(16) or
            this[1].asULong(8) or
            this[0].asULong(0)
}
public fun ByteArray.readUInt64BE(): ULong? {
    if (size < 8)
        return null

    return this[0].asULong(56) or
            this[1].asULong(48) or
            this[2].asULong(40) or
            this[3].asULong(32) or
            this[4].asULong(24) or
            this[5].asULong(16) or
            this[6].asULong(8) or
            this[7].asULong(0)
}

public fun ByteArray.readInt32LE(): Int? {
    if (size < 4)
        return null

    return this[3].asInt(24) or
            this[2].asInt(16) or
            this[1].asInt(8) or
            this[0].asInt(0)
}
public fun ByteArray.readInt32BE(): Int? {
    if (size < 4)
        return null

    return this[0].asInt(24) or
            this[1].asInt(16) or
            this[2].asInt(8) or
            this[3].asInt(0)
}

public fun ByteArray.readUInt32LE(): UInt? {
    if (size < 4)
        return null

    return this[3].asUInt(24) or
            this[2].asUInt(16) or
            this[1].asUInt(8) or
            this[0].asUInt(0)
}
public fun ByteArray.readUInt32BE(): UInt? {
    if (size < 4)
        return null

    return this[0].asUInt(24) or
            this[1].asUInt(16) or
            this[2].asUInt(8) or
            this[3].asUInt(0)
}

public fun ByteArray.readInt24BE(): Int? {
    if (size < 3)
        return null

    return this[0].asInt(16) or
            this[1].asInt(8) or
            this[2].asInt(0)
}

public fun ByteArray.readInt16LE(): Int? {
    if (size < 2)
        return null

    return this[1].asInt(8) or this[0].asInt(0)
}
public fun ByteArray.readInt16BE(): Int? {
    if (size < 2)
        return null

    return this[0].asInt(8) or this[1].asInt(0)
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public fun ByteArray.readVariableInt16(): Int? {
    val first = this[0].asInt()
    if (first < 0x80) return first
    return (first and 0x7F) or this[1].asInt(7)
}

public inline fun ByteArray.readFloatBE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun ByteArray.readFloatLE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun ByteArray.readFloat32BE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun ByteArray.readFloat32LE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun ByteArray.readDoubleBE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun ByteArray.readDoubleLE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }
public inline fun ByteArray.readFloat64BE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun ByteArray.readFloat64LE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }

/** Read from Index */

public fun ByteArray.readInt64LE(index: Int): Long? {
    if (size - 8 < index)
        return null

    val a = this[index].toLong() and 0xFF
    val b = this[index + 1].toLong() and 0xFF
    val c = this[index + 2].toLong() and 0xFF
    val d = this[index + 3].toLong() and 0xFF
    val e = this[index + 4].toLong() and 0xFF
    val f = this[index + 5].toLong() and 0xFF
    val g = this[index + 6].toLong() and 0xFF
    val h = this[index + 7].toLong() and 0xFF

    return (h shl 56) or (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}
public fun ByteArray.readInt64BE(index: Int): Long? {
    if (size - 8 < index)
        return null

    val a = this[index].toLong() and 0xFF
    val b = this[index + 1].toLong() and 0xFF
    val c = this[index + 2].toLong() and 0xFF
    val d = this[index + 3].toLong() and 0xFF
    val e = this[index + 4].toLong() and 0xFF
    val f = this[index + 5].toLong() and 0xFF
    val g = this[index + 6].toLong() and 0xFF
    val h = this[index + 7].toLong() and 0xFF

    return (a shl 56) or (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

@ExperimentalUnsignedTypes
public fun ByteArray.readUInt64LE(index: Int): ULong? {
    if (size - 8 < index)
        return null

    val a = this[index].toULong() and 0xFFu
    val b = this[index + 1].toULong() and 0xFFu
    val c = this[index + 2].toULong() and 0xFFu
    val d = this[index + 3].toULong() and 0xFFu
    val e = this[index + 4].toULong() and 0xFFu
    val f = this[index + 5].toULong() and 0xFFu
    val g = this[index + 6].toULong() and 0xFFu
    val h = this[index + 7].toULong() and 0xFFu

    return (h shl 56) or (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}
@ExperimentalUnsignedTypes
public fun ByteArray.readUInt64BE(index: Int): ULong? {
    if (size - 8 < index)
        return null

    val a = this[index].toULong() and 0xFFu
    val b = this[index + 1].toULong() and 0xFFu
    val c = this[index + 2].toULong() and 0xFFu
    val d = this[index + 3].toULong() and 0xFFu
    val e = this[index + 4].toULong() and 0xFFu
    val f = this[index + 5].toULong() and 0xFFu
    val g = this[index + 6].toULong() and 0xFFu
    val h = this[index + 7].toULong() and 0xFFu

    return (a shl 56) or (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public fun ByteArray.readInt32LE(index: Int): Int? {
    if (size - 4 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF
    val d = this[index + 3].toInt() and 0xFF

    return (d shl 24) or (c shl 16) or (b shl 8) or a
}
public fun ByteArray.readInt32BE(index: Int): Int? {
    if (size - 4 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF
    val d = this[index + 3].toInt() and 0xFF

    return (a shl 24) or (b shl 16) or (c shl 8) or d
}

@ExperimentalUnsignedTypes
public fun ByteArray.readUInt32LE(index: Int): UInt? {
    if (size - 4 < index)
        return null

    val a = this[index].toUInt() and 0xFFu
    val b = this[index + 1].toUInt() and 0xFFu
    val c = this[index + 2].toUInt() and 0xFFu
    val d = this[index + 3].toUInt() and 0xFFu

    return ((d shl 24) or (c shl 16) or (b shl 8) or a)
}
@ExperimentalUnsignedTypes
public fun ByteArray.readUInt32BE(index: Int): UInt? {
    if (size - 4 < index)
        return null

    val a = this[index].toUInt() and 0xFFu
    val b = this[index + 1].toUInt() and 0xFFu
    val c = this[index + 2].toUInt() and 0xFFu
    val d = this[index + 3].toUInt() and 0xFFu

    return ((a shl 24) or (b shl 16) or (c shl 8) or d)
}

public fun ByteArray.readInt24BE(index: Int): Int? {
    if (size - 3 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF

    return (a shl 16) or (b shl 8) or c
}

public fun ByteArray.readInt16LE(index: Int): Int? {
    if (size - 2 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF

    return (b shl 8) or a
}
public fun ByteArray.readInt16BE(index: Int): Int? {
    if (size - 2 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF

    return (a shl 8) or b
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public fun ByteArray.readVariableInt16(index: Int): Int? {
    val first = this[index].asInt()
    if (first < 0x80) return first
    return (first and 0x7F) or (this.getOrNull(index + 1) ?: return null).asInt(7)
}

public fun ByteArray.readFloatBE(index: Int): Float? = this.readInt32BE(index)?.let { Float.fromBits(it) }
public fun ByteArray.readFloatLE(index: Int): Float? = this.readInt32LE(index)?.let { Float.fromBits(it) }
public fun ByteArray.readFloat32BE(index: Int): Float? = this.readInt32BE(index)?.let { Float.fromBits(it) }
public fun ByteArray.readFloat32LE(index: Int): Float? = this.readInt32LE(index)?.let { Float.fromBits(it) }
public fun ByteArray.readDoubleBE(index: Int): Double? = this.readInt64BE(index)?.let { Double.fromBits(it) }
public fun ByteArray.readDoubleLE(index: Int): Double? = this.readInt64LE(index)?.let { Double.fromBits(it) }
public fun ByteArray.readFloat64BE(index: Int): Double? = this.readInt64BE(index)?.let { Double.fromBits(it) }
public fun ByteArray.readFloat64LE(index: Int): Double? = this.readInt64LE(index)?.let { Double.fromBits(it) }


/** Write at base */

public fun ByteArray.writeInt64LE(num: Number): Number? {
    if (size < 8)
        return null
    
    val long = num.toLong()

    this[0] = long.asByte(0)
    this[1] = long.asByte(8)
    this[2] = long.asByte(16)
    this[3] = long.asByte(24)
    this[4] = long.asByte(32)
    this[5] = long.asByte(40)
    this[6] = long.asByte(48)
    this[7] = long.asByte(56)

    return long
}
public fun ByteArray.writeInt64BE(num: Number): Number? {
    if (size < 8)
        return null

    val long = num.toLong()

    this[0] = long.asByte(56)
    this[1] = long.asByte(48)
    this[2] = long.asByte(40)
    this[3] = long.asByte(32)
    this[4] = long.asByte(24)
    this[5] = long.asByte(16)
    this[6] = long.asByte(8)
    this[7] = long.asByte(0)

    return long
}

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(num: ULong): Number? = writeUInt64LE(num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(num: UInt): Number? = writeUInt64LE(num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(num: UShort): Number? = writeUInt64LE(num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(num: UByte): Number? = writeUInt64LE(num.toByte())

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(num: ULong): Number? = writeUInt64BE(num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(num: UInt): Number? = writeUInt64BE(num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(num: UShort): Number? = writeUInt64BE(num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(num: UByte): Number? = writeUInt64BE(num.toByte())

public inline fun ByteArray.writeUInt64LE(num: Number): Number? = writeInt64LE(num)
public inline fun ByteArray.writeUInt64BE(num: Number): Number? = writeInt64BE(num)

public fun ByteArray.writeInt32LE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    this[0] = int.asByte(0)
    this[1] = int.asByte(8)
    this[2] = int.asByte(16)
    this[3] = int.asByte(24)

    return int
}
public fun ByteArray.writeInt32BE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    this[0] = int.asByte(24)
    this[1] = int.asByte(16)
    this[2] = int.asByte(8)
    this[3] = int.asByte(0)

    return int
}

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(num: ULong): Number? = writeUInt32LE(num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(num: UInt): Number? = writeUInt32LE(num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(num: UShort): Number? = writeUInt32LE(num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(num: UByte): Number? = writeUInt32LE(num.toByte())

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(num: ULong): Number? = writeUInt32LE(num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(num: UInt): Number? = writeUInt32LE(num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(num: UShort): Number? = writeUInt32LE(num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(num: UByte): Number? = writeUInt32LE(num.toByte())


public inline fun ByteArray.writeUInt32LE(num: Number): Number? = writeInt32LE(num)
public inline fun ByteArray.writeUInt32BE(num: Number): Number? = writeInt32BE(num)

//public fun ByteArray.writeInt24BE(num: Number): Number? {
//    if (size < 3)
//        return null
//
////    val word = num.to
//
//    this[0] =
//
//    return this[0].asInt(16) or
//            this[1].asInt(8) or
//            this[2].asInt(0)
//}

public fun ByteArray.writeInt16LE(num: Number): Number? {
    if (size < 2)
        return null

    val short = num.toShort()

    this[0] = short.asByte(0)
    this[1] = short.asByte(8)

    return short
}
public fun ByteArray.writeInt16BE(num: Number): Number? {
    if (size < 2)
        return null

    val short = num.toShort()

    this[0] = short.asByte(8)
    this[1] = short.asByte(0)

    return short
}

public fun ByteArray.writeVariableInt16(num: Number): Number? {
    val short = num.toShort()

    if (short < 0x80) {
        this[0] = short.asByte()
    } else {
        this[0] = short.or(0x80).asByte()
        this[1] = short.asByte(7)
    }

    return short
}

public inline fun ByteArray.writeFloatBE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun ByteArray.writeFloatLE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun ByteArray.writeFloat32BE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun ByteArray.writeFloat32LE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun ByteArray.writeDoubleBE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun ByteArray.writeDoubleLE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())
public inline fun ByteArray.writeFloat64BE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun ByteArray.writeFloat64LE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())


/** Read from Index */


public fun ByteArray.writeInt64LE(index: Int, num: Number): Number? {
    if (size - 8 < index)
        return null

    val long = num.toLong()

    this[index + 0] = long.asByte(0)
    this[index + 1] = long.asByte(8)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(32)
    this[index + 5] = long.asByte(40)
    this[index + 6] = long.asByte(48)
    this[index + 7] = long.asByte(56)

    return long
}
public fun ByteArray.writeInt64BE(index: Int, num: Number): Number? {
    if (size - 8 < index)
        return null

    val long = num.toLong()

    this[index + 0] = long.asByte(56)
    this[index + 1] = long.asByte(48)
    this[index + 2] = long.asByte(40)
    this[index + 3] = long.asByte(32)
    this[index + 4] = long.asByte(24)
    this[index + 5] = long.asByte(16)
    this[index + 6] = long.asByte(8)
    this[index + 7] = long.asByte(0)

    return long
}

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(index: Int, num: ULong): Number? = writeInt64LE(index, num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(index: Int, num: UInt): Number? = writeInt64LE(index, num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(index: Int, num: UShort): Number? = writeInt64LE(index, num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64LE(index: Int, num: UByte): Number? = writeInt64LE(index, num.toByte())
public inline fun ByteArray.writeUInt64LE(index: Int, num: Number): Number? = writeInt64LE(index, num)
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(index: Int, num: ULong): Number? = writeInt64BE(index, num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(index: Int, num: UInt): Number? = writeInt64BE(index, num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(index: Int, num: UShort): Number? = writeInt64BE(index, num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt64BE(index: Int, num: UByte): Number? = writeInt64BE(index, num.toByte())
public inline fun ByteArray.writeUInt64BE(index: Int, num: Number): Number? = writeInt64BE(index, num)

public fun ByteArray.writeInt32LE(index: Int, num: Number): Number? {
    if (size - 4 < index)
        return null

    val int = num.toInt()

    this[index + 0] = int.asByte(0)
    this[index + 1] = int.asByte(8)
    this[index + 2] = int.asByte(16)
    this[index + 3] = int.asByte(24)

    return int
}
public fun ByteArray.writeInt32BE(index: Int, num: Number): Number? {
    if (size - 4 < index)
        return null

    val int = num.toInt()

    this[index + 0] = int.asByte(24)
    this[index + 1] = int.asByte(16)
    this[index + 2] = int.asByte(8)
    this[index + 3] = int.asByte(0)

    return int
}

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(index: Int, num: ULong): Number? = writeUInt32LE(index, num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(index: Int, num: UInt): Number? = writeUInt32LE(index, num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(index: Int, num: UShort): Number? = writeUInt32LE(index, num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32LE(index: Int, num: UByte): Number? = writeUInt32LE(index, num.toByte())
public inline fun ByteArray.writeUInt32LE(index: Int, num: Number): Number? = writeInt32LE(index, num)

@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(index: Int, num: ULong): Number? = writeUInt32BE(index, num.toLong())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(index: Int, num: UInt): Number? = writeUInt32BE(index, num.toInt())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(index: Int, num: UShort): Number? = writeUInt32BE(index, num.toShort())
@ExperimentalUnsignedTypes
public inline fun ByteArray.writeUInt32BE(index: Int, num: UByte): Number? = writeUInt32BE(index, num.toByte())
public inline fun ByteArray.writeUInt32BE(index: Int, num: Number): Number? = writeInt32BE(index, num)

public fun ByteArray.writeInt16LE(index: Int, num: Number): Number? {
    if (size - 2 < index)
        return null

    val short = num.toShort()

    this[index + 0] = short.asByte(0)
    this[index + 1] = short.asByte(8)

    return short
}
public fun ByteArray.writeInt16BE(index: Int, num: Number): Number? {
    if (size - 2 < index)
        return null

    val short = num.toShort()

    this[index + 0] = short.asByte(0)
    this[index + 1] = short.asByte(8)

    return short
}

public fun ByteArray.writeVariableInt16(index: Int, num: Number): Number? {
    val short = num.toShort()

    if (short < 0x80 && size - 1 < index) {
        this[index] = short.asByte()
    } else if (size - 2 < index) {
        this[index] = short.or(0x80).asByte()
        this[index + 1] = short.asByte(7)
    } else {
        return null
    }

    return short
}

public inline fun ByteArray.writeFloatBE(index: Int, num: Number): Number? = this.writeInt32BE(index, num.toFloat().toBits())
public inline fun ByteArray.writeFloatLE(index: Int, num: Number): Number? = this.writeInt32LE(index, num.toFloat().toBits())
public inline fun ByteArray.writeFloat32BE(index: Int, num: Number): Number? = this.writeInt32BE(index, num.toFloat().toBits())
public inline fun ByteArray.writeFloat32LE(index: Int, num: Number): Number? = this.writeInt32LE(index, num.toFloat().toBits())
public inline fun ByteArray.writeDoubleBE(index: Int, num: Number): Number? = this.writeInt64BE(index, num.toDouble().toBits())
public inline fun ByteArray.writeDoubleLE(index: Int, num: Number): Number? = this.writeInt64LE(index, num.toDouble().toBits())
public inline fun ByteArray.writeFloat64BE(index: Int, num: Number): Number? = this.writeInt64BE(index, num.toDouble().toBits())
public inline fun ByteArray.writeFloat64LE(index: Int, num: Number): Number? = this.writeInt64LE(index, num.toDouble().toBits())