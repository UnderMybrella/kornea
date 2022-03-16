package dev.brella.kornea.io.native

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.toolkit.common.*
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get
import kotlinx.cinterop.set
import kotlin.experimental.or

/** Read from base */

public fun CPointer<ByteVar>.readInt64LE(): Long? {
    return this[7].asLong(56) or
            this[6].asLong(48) or
            this[5].asLong(40) or
            this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun CPointer<ByteVar>.readInt64BE(): Long? {
    return this[0].asLong(56) or
            this[1].asLong(48) or
            this[2].asLong(40) or
            this[3].asLong(32) or
            this[4].asLong(24) or
            this[5].asLong(16) or
            this[6].asLong(8) or
            this[7].asLong(0)
}

public fun CPointer<ByteVar>.readUInt64LE(): ULong? {
    return this[7].asULong(56) or
            this[6].asULong(48) or
            this[5].asULong(40) or
            this[4].asULong(32) or
            this[3].asULong(24) or
            this[2].asULong(16) or
            this[1].asULong(8) or
            this[0].asULong(0)
}
public fun CPointer<ByteVar>.readUInt64BE(): ULong? {
    return this[0].asULong(56) or
            this[1].asULong(48) or
            this[2].asULong(40) or
            this[3].asULong(32) or
            this[4].asULong(24) or
            this[5].asULong(16) or
            this[6].asULong(8) or
            this[7].asULong(0)
}

public fun CPointer<ByteVar>.readInt56LE(): Long? {
    return this[6].asLong(48) or
            this[5].asLong(40) or
            this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun CPointer<ByteVar>.readInt56BE(): Long? {
    return this[0].asLong(48) or
            this[1].asLong(40) or
            this[2].asLong(32) or
            this[3].asLong(24) or
            this[4].asLong(16) or
            this[5].asLong(8) or
            this[6].asLong(0)
}

public fun CPointer<ByteVar>.readInt48LE(): Long? {
    return this[5].asLong(40) or
            this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun CPointer<ByteVar>.readInt48BE(): Long? {
    return this[0].asLong(40) or
            this[1].asLong(32) or
            this[2].asLong(24) or
            this[3].asLong(16) or
            this[4].asLong(8) or
            this[5].asLong(0)
}

public fun CPointer<ByteVar>.readInt40LE(): Long? {
    return this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun CPointer<ByteVar>.readInt40BE(): Long? {
    return this[0].asLong(32) or
            this[1].asLong(24) or
            this[2].asLong(16) or
            this[3].asLong(8) or
            this[4].asLong(0)
}

public fun CPointer<ByteVar>.readInt32LE(): Int? {
    return this[3].asInt(24) or
            this[2].asInt(16) or
            this[1].asInt(8) or
            this[0].asInt(0)
}
public fun CPointer<ByteVar>.readInt32BE(): Int? {
    return this[0].asInt(24) or
            this[1].asInt(16) or
            this[2].asInt(8) or
            this[3].asInt(0)
}

public fun CPointer<ByteVar>.readUInt32LE(): UInt? {
    return this[3].asUInt(24) or
            this[2].asUInt(16) or
            this[1].asUInt(8) or
            this[0].asUInt(0)
}
public fun CPointer<ByteVar>.readUInt32BE(): UInt? {
    return this[0].asUInt(24) or
            this[1].asUInt(16) or
            this[2].asUInt(8) or
            this[3].asUInt(0)
}

public fun CPointer<ByteVar>.readInt24LE(): Int? =
    this[0].asInt(0) or
            this[1].asInt(8) or
            this[2].asInt(16)

public fun CPointer<ByteVar>.readInt24BE(): Int? =
    this[0].asInt(16) or
            this[1].asInt(8) or
            this[2].asInt(0)

public fun CPointer<ByteVar>.readInt16LE(): Int? =
    this[1].asInt(8) or this[0].asInt(0)

public fun CPointer<ByteVar>.readInt16BE(): Int? =
    this[0].asInt(8) or this[1].asInt(0)

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public fun CPointer<ByteVar>.readVariableInt16(): Int? {
    val first = this[0].asInt()
    if (first < 0x80) return first
    return (first and 0x7F) or this[1].asInt(7)
}

public inline fun CPointer<ByteVar>.readFloatBE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun CPointer<ByteVar>.readFloatLE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun CPointer<ByteVar>.readFloat32BE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun CPointer<ByteVar>.readFloat32LE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun CPointer<ByteVar>.readDoubleBE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun CPointer<ByteVar>.readDoubleLE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }
public inline fun CPointer<ByteVar>.readFloat64BE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun CPointer<ByteVar>.readFloat64LE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }

/** Read from Index */

public fun CPointer<ByteVar>.readInt64LE(index: Int): Long? {
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
public fun CPointer<ByteVar>.readInt64BE(index: Int): Long? {
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

public fun CPointer<ByteVar>.readUInt64LE(index: Int): ULong? {
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
public fun CPointer<ByteVar>.readUInt64BE(index: Int): ULong? {
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

public fun CPointer<ByteVar>.readInt56LE(index: Int): Long? {
    return this[index + 6].asLong(48) or
            this[index + 5].asLong(40) or
            this[index + 4].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 1].asLong(8) or
            this[index + 0].asLong(0)
}
public fun CPointer<ByteVar>.readInt56BE(index: Int): Long? {
    return this[index + 0].asLong(48) or
            this[index + 1].asLong(40) or
            this[index + 2].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 4].asLong(16) or
            this[index + 5].asLong(8) or
            this[index + 6].asLong(0)
}

public fun CPointer<ByteVar>.readInt48LE(index: Int): Long? {
    return this[index + 5].asLong(40) or
            this[index + 4].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 1].asLong(8) or
            this[index + 0].asLong(0)
}
public fun CPointer<ByteVar>.readInt48BE(index: Int): Long? {
    return this[index + 0].asLong(40) or
            this[index + 1].asLong(32) or
            this[index + 2].asLong(24) or
            this[index + 3].asLong(16) or
            this[index + 4].asLong(8) or
            this[index + 5].asLong(0)
}

public fun CPointer<ByteVar>.readInt40LE(index: Int): Long? {
    return this[index + 4].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 1].asLong(8) or
            this[index + 0].asLong(0)
}
public fun CPointer<ByteVar>.readInt40BE(index: Int): Long? {
    return this[index + 0].asLong(32) or
            this[index + 1].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 3].asLong(8) or
            this[index + 4].asLong(0)
}

public fun CPointer<ByteVar>.readInt32LE(index: Int): Int? {
    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF
    val d = this[index + 3].toInt() and 0xFF

    return (d shl 24) or (c shl 16) or (b shl 8) or a
}
public fun CPointer<ByteVar>.readInt32BE(index: Int): Int? {
    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF
    val d = this[index + 3].toInt() and 0xFF

    return (a shl 24) or (b shl 16) or (c shl 8) or d
}

public fun CPointer<ByteVar>.readUInt32LE(index: Int): UInt? {
    val a = this[index].toUInt() and 0xFFu
    val b = this[index + 1].toUInt() and 0xFFu
    val c = this[index + 2].toUInt() and 0xFFu
    val d = this[index + 3].toUInt() and 0xFFu

    return ((d shl 24) or (c shl 16) or (b shl 8) or a)
}
public fun CPointer<ByteVar>.readUInt32BE(index: Int): UInt? {
    val a = this[index].toUInt() and 0xFFu
    val b = this[index + 1].toUInt() and 0xFFu
    val c = this[index + 2].toUInt() and 0xFFu
    val d = this[index + 3].toUInt() and 0xFFu

    return ((a shl 24) or (b shl 16) or (c shl 8) or d)
}

public fun CPointer<ByteVar>.readInt24LE(index: Int): Int? {
    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF

    return (c shl 16) or (b shl 8) or a
}

public fun CPointer<ByteVar>.readInt24BE(index: Int): Int? {
    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF

    return (a shl 16) or (b shl 8) or c
}

public fun CPointer<ByteVar>.readInt16LE(index: Int): Int? {
    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF

    return (b shl 8) or a
}
public fun CPointer<ByteVar>.readInt16BE(index: Int): Int? {
    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF

    return (a shl 8) or b
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public fun CPointer<ByteVar>.readVariableInt16(index: Int): Int? {
    val first = this[index].asInt()
    if (first < 0x80) return first
    return (first and 0x7F) or this[index + 1].asInt(7)
}

public fun CPointer<ByteVar>.readFloatBE(index: Int): Float? = this.readInt32BE(index)?.let { Float.fromBits(it) }
public fun CPointer<ByteVar>.readFloatLE(index: Int): Float? = this.readInt32LE(index)?.let { Float.fromBits(it) }
public fun CPointer<ByteVar>.readFloat32BE(index: Int): Float? = this.readInt32BE(index)?.let { Float.fromBits(it) }
public fun CPointer<ByteVar>.readFloat32LE(index: Int): Float? = this.readInt32LE(index)?.let { Float.fromBits(it) }
public fun CPointer<ByteVar>.readDoubleBE(index: Int): Double? = this.readInt64BE(index)?.let { Double.fromBits(it) }
public fun CPointer<ByteVar>.readDoubleLE(index: Int): Double? = this.readInt64LE(index)?.let { Double.fromBits(it) }
public fun CPointer<ByteVar>.readFloat64BE(index: Int): Double? = this.readInt64BE(index)?.let { Double.fromBits(it) }
public fun CPointer<ByteVar>.readFloat64LE(index: Int): Double? = this.readInt64LE(index)?.let { Double.fromBits(it) }


/** Write at base */

public fun CPointer<ByteVar>.writeInt64LE(num: Number): Number? {
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
public fun CPointer<ByteVar>.writeInt64BE(num: Number): Number? {
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

public inline fun CPointer<ByteVar>.writeUInt64LE(num: ULong): Number? = writeUInt64LE(num.toLong())
public inline fun CPointer<ByteVar>.writeUInt64LE(num: UInt): Number? = writeUInt64LE(num.toInt())
public inline fun CPointer<ByteVar>.writeUInt64LE(num: UShort): Number? = writeUInt64LE(num.toShort())
public inline fun CPointer<ByteVar>.writeUInt64LE(num: UByte): Number? = writeUInt64LE(num.toByte())

public inline fun CPointer<ByteVar>.writeUInt64BE(num: ULong): Number? = writeUInt64BE(num.toLong())
public inline fun CPointer<ByteVar>.writeUInt64BE(num: UInt): Number? = writeUInt64BE(num.toInt())
public inline fun CPointer<ByteVar>.writeUInt64BE(num: UShort): Number? = writeUInt64BE(num.toShort())
public inline fun CPointer<ByteVar>.writeUInt64BE(num: UByte): Number? = writeUInt64BE(num.toByte())

public inline fun CPointer<ByteVar>.writeUInt64LE(num: Number): Number? = writeInt64LE(num)
public inline fun CPointer<ByteVar>.writeUInt64BE(num: Number): Number? = writeInt64BE(num)

public fun CPointer<ByteVar>.writeInt56LE(num: Number): Number? {
    val long = num.toLong()

    this[0] = long.asByte(0)
    this[1] = long.asByte(8)
    this[2] = long.asByte(16)
    this[3] = long.asByte(24)
    this[4] = long.asByte(32)
    this[5] = long.asByte(40)
    this[6] = long.asByte(48)

    return long
}
public fun CPointer<ByteVar>.writeInt56BE(num: Number): Number? {
    val long = num.toLong()

    this[0] = long.asByte(48)
    this[1] = long.asByte(40)
    this[2] = long.asByte(32)
    this[3] = long.asByte(24)
    this[4] = long.asByte(16)
    this[5] = long.asByte(8)
    this[6] = long.asByte(0)

    return long
}

public fun CPointer<ByteVar>.writeInt48LE(num: Number): Number? {
    val long = num.toLong()

    this[0] = long.asByte(0)
    this[1] = long.asByte(8)
    this[2] = long.asByte(16)
    this[3] = long.asByte(24)
    this[4] = long.asByte(32)
    this[5] = long.asByte(40)

    return long
}
public fun CPointer<ByteVar>.writeInt48BE(num: Number): Number? {
    val long = num.toLong()

    this[0] = long.asByte(40)
    this[1] = long.asByte(32)
    this[2] = long.asByte(24)
    this[3] = long.asByte(16)
    this[4] = long.asByte(8)
    this[5] = long.asByte(0)

    return long
}

public fun CPointer<ByteVar>.writeInt40LE(num: Number): Number? {
    val long = num.toLong()

    this[0] = long.asByte(0)
    this[1] = long.asByte(8)
    this[2] = long.asByte(16)
    this[3] = long.asByte(24)
    this[4] = long.asByte(32)

    return long
}
public fun CPointer<ByteVar>.writeInt40BE(num: Number): Number? {
    val long = num.toLong()

    this[0] = long.asByte(32)
    this[1] = long.asByte(24)
    this[2] = long.asByte(16)
    this[3] = long.asByte(8)
    this[4] = long.asByte(0)

    return long
}

public fun CPointer<ByteVar>.writeInt32LE(num: Number): Number? {
    val int = num.toInt()

    this[0] = int.asByte(0)
    this[1] = int.asByte(8)
    this[2] = int.asByte(16)
    this[3] = int.asByte(24)

    return int
}
public fun CPointer<ByteVar>.writeInt32BE(num: Number): Number? {
    val int = num.toInt()

    this[0] = int.asByte(24)
    this[1] = int.asByte(16)
    this[2] = int.asByte(8)
    this[3] = int.asByte(0)

    return int
}

public inline fun CPointer<ByteVar>.writeUInt32LE(num: ULong): Number? = writeUInt32LE(num.toLong())
public inline fun CPointer<ByteVar>.writeUInt32LE(num: UInt): Number? = writeUInt32LE(num.toInt())
public inline fun CPointer<ByteVar>.writeUInt32LE(num: UShort): Number? = writeUInt32LE(num.toShort())
public inline fun CPointer<ByteVar>.writeUInt32LE(num: UByte): Number? = writeUInt32LE(num.toByte())

public inline fun CPointer<ByteVar>.writeUInt32BE(num: ULong): Number? = writeUInt32LE(num.toLong())
public inline fun CPointer<ByteVar>.writeUInt32BE(num: UInt): Number? = writeUInt32LE(num.toInt())
public inline fun CPointer<ByteVar>.writeUInt32BE(num: UShort): Number? = writeUInt32LE(num.toShort())
public inline fun CPointer<ByteVar>.writeUInt32BE(num: UByte): Number? = writeUInt32LE(num.toByte())


public inline fun CPointer<ByteVar>.writeUInt32LE(num: Number): Number? = writeInt32LE(num)
public inline fun CPointer<ByteVar>.writeUInt32BE(num: Number): Number? = writeInt32BE(num)

public fun CPointer<ByteVar>.writeInt24LE(num: Number): Number? {
    val word = num.toInt() and 0xFFFFFF

    this[0] = word.asByte(0)
    this[1] = word.asByte(8)
    this[2] = word.asByte(16)

    return word
}

public fun CPointer<ByteVar>.writeInt24BE(num: Number): Number? {
    val word = num.toInt() and 0xFFFFFF

    this[0] = word.asByte(16)
    this[1] = word.asByte(8)
    this[2] = word.asByte(0)

    return word
}

public fun CPointer<ByteVar>.writeInt16LE(num: Number): Number? {
    val short = num.toShort()

    this[0] = short.asByte(0)
    this[1] = short.asByte(8)

    return short
}
public fun CPointer<ByteVar>.writeInt16BE(num: Number): Number? {
    val short = num.toShort()

    this[0] = short.asByte(8)
    this[1] = short.asByte(0)

    return short
}

public fun CPointer<ByteVar>.writeVariableInt16(num: Number): Number? {
    val short = num.toShort()

    if (short < 0x80) {
        this[0] = short.asByte()
    } else {
        this[0] = short.or(0x80).asByte()
        this[1] = short.asByte(7)
    }

    return short
}

public inline fun CPointer<ByteVar>.writeFloatBE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeFloatLE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeFloat32BE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeFloat32LE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeDoubleBE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun CPointer<ByteVar>.writeDoubleLE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())
public inline fun CPointer<ByteVar>.writeFloat64BE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun CPointer<ByteVar>.writeFloat64LE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())


/** Read from Index */


public fun CPointer<ByteVar>.writeInt64LE(index: Int, num: Number): Number? {
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
public fun CPointer<ByteVar>.writeInt64BE(index: Int, num: Number): Number? {
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

public inline fun CPointer<ByteVar>.writeUInt64LE(index: Int, num: ULong): Number? = writeInt64LE(index, num.toLong())
public inline fun CPointer<ByteVar>.writeUInt64LE(index: Int, num: UInt): Number? = writeInt64LE(index, num.toInt())
public inline fun CPointer<ByteVar>.writeUInt64LE(index: Int, num: UShort): Number? = writeInt64LE(index, num.toShort())
public inline fun CPointer<ByteVar>.writeUInt64LE(index: Int, num: UByte): Number? = writeInt64LE(index, num.toByte())
public inline fun CPointer<ByteVar>.writeUInt64LE(index: Int, num: Number): Number? = writeInt64LE(index, num)
public inline fun CPointer<ByteVar>.writeUInt64BE(index: Int, num: ULong): Number? = writeInt64BE(index, num.toLong())
public inline fun CPointer<ByteVar>.writeUInt64BE(index: Int, num: UInt): Number? = writeInt64BE(index, num.toInt())
public inline fun CPointer<ByteVar>.writeUInt64BE(index: Int, num: UShort): Number? = writeInt64BE(index, num.toShort())
public inline fun CPointer<ByteVar>.writeUInt64BE(index: Int, num: UByte): Number? = writeInt64BE(index, num.toByte())
public inline fun CPointer<ByteVar>.writeUInt64BE(index: Int, num: Number): Number? = writeInt64BE(index, num)

public fun CPointer<ByteVar>.writeInt56LE(index: Int, num: Number): Number? {
    val long = num.toLong()

    this[index + 0] = long.asByte(0)
    this[index + 1] = long.asByte(8)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(32)
    this[index + 5] = long.asByte(40)
    this[index + 6] = long.asByte(48)

    return long
}
public fun CPointer<ByteVar>.writeInt56BE(index: Int, num: Number): Number? {
    val long = num.toLong()

    this[index + 0] = long.asByte(48)
    this[index + 1] = long.asByte(40)
    this[index + 2] = long.asByte(32)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(16)
    this[index + 5] = long.asByte(8)
    this[index + 6] = long.asByte(0)

    return long
}

public fun CPointer<ByteVar>.writeInt48LE(index: Int, num: Number): Number? {
    val long = num.toLong()

    this[index + 0] = long.asByte(0)
    this[index + 1] = long.asByte(8)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(32)
    this[index + 5] = long.asByte(40)

    return long
}
public fun CPointer<ByteVar>.writeInt48BE(index: Int, num: Number): Number? {
    val long = num.toLong()

    this[index + 0] = long.asByte(40)
    this[index + 1] = long.asByte(32)
    this[index + 2] = long.asByte(24)
    this[index + 3] = long.asByte(16)
    this[index + 4] = long.asByte(8)
    this[index + 5] = long.asByte(0)

    return long
}

public fun CPointer<ByteVar>.writeInt40LE(index: Int, num: Number): Number? {
    val long = num.toLong()

    this[index + 0] = long.asByte(0)
    this[index + 1] = long.asByte(8)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(32)

    return long
}
public fun CPointer<ByteVar>.writeInt40BE(index: Int, num: Number): Number? {
    val long = num.toLong()

    this[index + 0] = long.asByte(32)
    this[index + 1] = long.asByte(24)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(8)
    this[index + 4] = long.asByte(0)

    return long
}

public fun CPointer<ByteVar>.writeInt32LE(index: Int, num: Number): Number? {
    val int = num.toInt()

    this[index + 0] = int.asByte(0)
    this[index + 1] = int.asByte(8)
    this[index + 2] = int.asByte(16)
    this[index + 3] = int.asByte(24)

    return int
}
public fun CPointer<ByteVar>.writeInt32BE(index: Int, num: Number): Number? {
    val int = num.toInt()

    this[index + 0] = int.asByte(24)
    this[index + 1] = int.asByte(16)
    this[index + 2] = int.asByte(8)
    this[index + 3] = int.asByte(0)

    return int
}

public inline fun CPointer<ByteVar>.writeUInt32LE(index: Int, num: ULong): Number? = writeUInt32LE(index, num.toLong())
public inline fun CPointer<ByteVar>.writeUInt32LE(index: Int, num: UInt): Number? = writeUInt32LE(index, num.toInt())
public inline fun CPointer<ByteVar>.writeUInt32LE(index: Int, num: UShort): Number? = writeUInt32LE(index, num.toShort())
public inline fun CPointer<ByteVar>.writeUInt32LE(index: Int, num: UByte): Number? = writeUInt32LE(index, num.toByte())
public inline fun CPointer<ByteVar>.writeUInt32LE(index: Int, num: Number): Number? = writeInt32LE(index, num)

public inline fun CPointer<ByteVar>.writeUInt32BE(index: Int, num: ULong): Number? = writeUInt32BE(index, num.toLong())
public inline fun CPointer<ByteVar>.writeUInt32BE(index: Int, num: UInt): Number? = writeUInt32BE(index, num.toInt())
public inline fun CPointer<ByteVar>.writeUInt32BE(index: Int, num: UShort): Number? = writeUInt32BE(index, num.toShort())
public inline fun CPointer<ByteVar>.writeUInt32BE(index: Int, num: UByte): Number? = writeUInt32BE(index, num.toByte())
public inline fun CPointer<ByteVar>.writeUInt32BE(index: Int, num: Number): Number? = writeInt32BE(index, num)

public fun CPointer<ByteVar>.writeInt24LE(index: Int, num: Number): Number? {
    val word = num.toInt() and 0xFFFFFF

    this[index + 0] = word.asByte(0)
    this[index + 1] = word.asByte(8)
    this[index + 2] = word.asByte(16)

    return word
}

public fun CPointer<ByteVar>.writeInt24BE(index: Int, num: Number): Number? {
    val word = num.toInt() and 0xFFFFFF

    this[index + 0] = word.asByte(16)
    this[index + 1] = word.asByte(8)
    this[index + 2] = word.asByte(0)

    return word
}

public fun CPointer<ByteVar>.writeInt16LE(index: Int, num: Number): Number? {
    val short = num.toShort()

    this[index + 0] = short.asByte(0)
    this[index + 1] = short.asByte(8)

    return short
}
public fun CPointer<ByteVar>.writeInt16BE(index: Int, num: Number): Number? {
    val short = num.toShort()

    this[index + 0] = short.asByte(0)
    this[index + 1] = short.asByte(8)

    return short
}

public fun CPointer<ByteVar>.writeVariableInt16(index: Int, num: Number): Number? {
    val short = num.toShort()

    if (short < 0x80) {
        this[index] = short.asByte()
    } else {
        this[index] = short.or(0x80).asByte()
        this[index + 1] = short.asByte(7)
    }

    return short
}

public inline fun CPointer<ByteVar>.writeFloatBE(index: Int, num: Number): Number? = this.writeInt32BE(index, num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeFloatLE(index: Int, num: Number): Number? = this.writeInt32LE(index, num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeFloat32BE(index: Int, num: Number): Number? = this.writeInt32BE(index, num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeFloat32LE(index: Int, num: Number): Number? = this.writeInt32LE(index, num.toFloat().toBits())
public inline fun CPointer<ByteVar>.writeDoubleBE(index: Int, num: Number): Number? = this.writeInt64BE(index, num.toDouble().toBits())
public inline fun CPointer<ByteVar>.writeDoubleLE(index: Int, num: Number): Number? = this.writeInt64LE(index, num.toDouble().toBits())
public inline fun CPointer<ByteVar>.writeFloat64BE(index: Int, num: Number): Number? = this.writeInt64BE(index, num.toDouble().toBits())
public inline fun CPointer<ByteVar>.writeFloat64LE(index: Int, num: Number): Number? = this.writeInt64LE(index, num.toDouble().toBits())