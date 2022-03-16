@file:Suppress("DuplicatedCode")

package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.toolkit.common.*
import kotlin.experimental.or

/** Read from base */

public fun List<Byte>.readInt64LE(): Long? {
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
public fun List<Byte>.readInt64BE(): Long? {
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

public fun List<Byte>.readUInt64LE(): ULong? {
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
public fun List<Byte>.readUInt64BE(): ULong? {
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

public fun List<Byte>.readInt56LE(): Long? {
    if (size < 7)
        return null

    return this[6].asLong(48) or
            this[5].asLong(40) or
            this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun List<Byte>.readInt56BE(): Long? {
    if (size < 7)
        return null

    return this[0].asLong(48) or
            this[1].asLong(40) or
            this[2].asLong(32) or
            this[3].asLong(24) or
            this[4].asLong(16) or
            this[5].asLong(8) or
            this[6].asLong(0)
}

public fun List<Byte>.readInt48LE(): Long? {
    if (size < 6)
        return null

    return this[5].asLong(40) or
            this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun List<Byte>.readInt48BE(): Long? {
    if (size < 6)
        return null

    return this[0].asLong(40) or
            this[1].asLong(32) or
            this[2].asLong(24) or
            this[3].asLong(16) or
            this[4].asLong(8) or
            this[5].asLong(0)
}

public fun List<Byte>.readInt40LE(): Long? {
    if (size < 5)
        return null

    return this[4].asLong(32) or
            this[3].asLong(24) or
            this[2].asLong(16) or
            this[1].asLong(8) or
            this[0].asLong(0)
}
public fun List<Byte>.readInt40BE(): Long? {
    if (size < 5)
        return null

    return this[0].asLong(32) or
            this[1].asLong(24) or
            this[2].asLong(16) or
            this[3].asLong(8) or
            this[4].asLong(0)
}

public fun List<Byte>.readInt32LE(): Int? {
    if (size < 4)
        return null

    return this[3].asInt(24) or
            this[2].asInt(16) or
            this[1].asInt(8) or
            this[0].asInt(0)
}
public fun List<Byte>.readInt32BE(): Int? {
    if (size < 4)
        return null

    return this[0].asInt(24) or
            this[1].asInt(16) or
            this[2].asInt(8) or
            this[3].asInt(0)
}

public fun List<Byte>.readUInt32LE(): UInt? {
    if (size < 4)
        return null

    return this[3].asUInt(24) or
            this[2].asUInt(16) or
            this[1].asUInt(8) or
            this[0].asUInt(0)
}
public fun List<Byte>.readUInt32BE(): UInt? {
    if (size < 4)
        return null

    return this[0].asUInt(24) or
            this[1].asUInt(16) or
            this[2].asUInt(8) or
            this[3].asUInt(0)
}

public fun List<Byte>.readInt24LE(): Int? {
    if (size < 3) return null

    return this[0].asInt(0) or
            this[1].asInt(8) or
            this[2].asInt(16)
}

public fun List<Byte>.readInt24BE(): Int? {
    if (size < 3)
        return null

    return this[0].asInt(16) or
            this[1].asInt(8) or
            this[2].asInt(0)
}

public fun List<Byte>.readInt16LE(): Int? {
    if (size < 2)
        return null

    return this[1].asInt(8) or this[0].asInt(0)
}
public fun List<Byte>.readInt16BE(): Int? {
    if (size < 2)
        return null

    return this[0].asInt(8) or this[1].asInt(0)
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public fun List<Byte>.readVariableInt16(): Int? {
    if (isEmpty()) return null

    val first = this[0].asInt()
    if (first < 0x80) return first
    else if (size < 2) return null

    return (first and 0x7F) or this[1].asInt(7)
}

public inline fun List<Byte>.readFloatBE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun List<Byte>.readFloatLE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun List<Byte>.readFloat32BE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun List<Byte>.readFloat32LE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun List<Byte>.readDoubleBE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun List<Byte>.readDoubleLE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }
public inline fun List<Byte>.readFloat64BE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun List<Byte>.readFloat64LE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }

/** Read from Index */

public fun List<Byte>.readInt64LE(index: Int): Long? {
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
public fun List<Byte>.readInt64BE(index: Int): Long? {
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

public fun List<Byte>.readUInt64LE(index: Int): ULong? {
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
public fun List<Byte>.readUInt64BE(index: Int): ULong? {
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

public fun List<Byte>.readInt56LE(index: Int): Long? {
    if (size < 7)
        return null

    return this[index + 6].asLong(48) or
            this[index + 5].asLong(40) or
            this[index + 4].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 1].asLong(8) or
            this[index + 0].asLong(0)
}
public fun List<Byte>.readInt56BE(index: Int): Long? {
    if (size < 7)
        return null

    return this[index + 0].asLong(48) or
            this[index + 1].asLong(40) or
            this[index + 2].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 4].asLong(16) or
            this[index + 5].asLong(8) or
            this[index + 6].asLong(0)
}

public fun List<Byte>.readInt48LE(index: Int): Long? {
    if (size < 6)
        return null

    return this[index + 5].asLong(40) or
            this[index + 4].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 1].asLong(8) or
            this[index + 0].asLong(0)
}
public fun List<Byte>.readInt48BE(index: Int): Long? {
    if (size < 6)
        return null

    return this[index + 0].asLong(40) or
            this[index + 1].asLong(32) or
            this[index + 2].asLong(24) or
            this[index + 3].asLong(16) or
            this[index + 4].asLong(8) or
            this[index + 5].asLong(0)
}

public fun List<Byte>.readInt40LE(index: Int): Long? {
    if (size < 5)
        return null

    return this[index + 4].asLong(32) or
            this[index + 3].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 1].asLong(8) or
            this[index + 0].asLong(0)
}
public fun List<Byte>.readInt40BE(index: Int): Long? {
    if (size < 5)
        return null

    return this[index + 0].asLong(32) or
            this[index + 1].asLong(24) or
            this[index + 2].asLong(16) or
            this[index + 3].asLong(8) or
            this[index + 4].asLong(0)
}

public fun List<Byte>.readInt32LE(index: Int): Int? {
    if (size - 4 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF
    val d = this[index + 3].toInt() and 0xFF

    return (d shl 24) or (c shl 16) or (b shl 8) or a
}
public fun List<Byte>.readInt32BE(index: Int): Int? {
    if (size - 4 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF
    val d = this[index + 3].toInt() and 0xFF

    return (a shl 24) or (b shl 16) or (c shl 8) or d
}

public fun List<Byte>.readUInt32LE(index: Int): UInt? {
    if (size - 4 < index)
        return null

    val a = this[index].toUInt() and 0xFFu
    val b = this[index + 1].toUInt() and 0xFFu
    val c = this[index + 2].toUInt() and 0xFFu
    val d = this[index + 3].toUInt() and 0xFFu

    return ((d shl 24) or (c shl 16) or (b shl 8) or a)
}
public fun List<Byte>.readUInt32BE(index: Int): UInt? {
    if (size - 4 < index)
        return null

    val a = this[index].toUInt() and 0xFFu
    val b = this[index + 1].toUInt() and 0xFFu
    val c = this[index + 2].toUInt() and 0xFFu
    val d = this[index + 3].toUInt() and 0xFFu

    return ((a shl 24) or (b shl 16) or (c shl 8) or d)
}

public fun List<Byte>.readInt24LE(index: Int): Int? {
    if (size - 3 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF

    return (c shl 16) or (b shl 8) or a
}

public fun List<Byte>.readInt24BE(index: Int): Int? {
    if (size - 3 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF
    val c = this[index + 2].toInt() and 0xFF

    return (a shl 16) or (b shl 8) or c
}

public fun List<Byte>.readInt16LE(index: Int): Int? {
    if (size - 2 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF

    return (b shl 8) or a
}
public fun List<Byte>.readInt16BE(index: Int): Int? {
    if (size - 2 < index)
        return null

    val a = this[index].toInt() and 0xFF
    val b = this[index + 1].toInt() and 0xFF

    return (a shl 8) or b
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public fun List<Byte>.readVariableInt16(index: Int): Int? {
    val first = this[index].asInt()
    if (first < 0x80) return first
    return (first and 0x7F) or (this.getOrNull(index + 1) ?: return null).asInt(7)
}

public fun List<Byte>.readFloatBE(index: Int): Float? = this.readInt32BE(index)?.let { Float.fromBits(it) }
public fun List<Byte>.readFloatLE(index: Int): Float? = this.readInt32LE(index)?.let { Float.fromBits(it) }
public fun List<Byte>.readFloat32BE(index: Int): Float? = this.readInt32BE(index)?.let { Float.fromBits(it) }
public fun List<Byte>.readFloat32LE(index: Int): Float? = this.readInt32LE(index)?.let { Float.fromBits(it) }
public fun List<Byte>.readDoubleBE(index: Int): Double? = this.readInt64BE(index)?.let { Double.fromBits(it) }
public fun List<Byte>.readDoubleLE(index: Int): Double? = this.readInt64LE(index)?.let { Double.fromBits(it) }
public fun List<Byte>.readFloat64BE(index: Int): Double? = this.readInt64BE(index)?.let { Double.fromBits(it) }
public fun List<Byte>.readFloat64LE(index: Int): Double? = this.readInt64LE(index)?.let { Double.fromBits(it) }


/** Write at base */

public fun MutableList<Byte>.writeInt64LE(num: Number): Number? {
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
public fun MutableList<Byte>.writeInt64BE(num: Number): Number? {
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

public inline fun MutableList<Byte>.writeUInt64LE(num: ULong): Number? = writeUInt64LE(num.toLong())
public inline fun MutableList<Byte>.writeUInt64LE(num: UInt): Number? = writeUInt64LE(num.toInt())
public inline fun MutableList<Byte>.writeUInt64LE(num: UShort): Number? = writeUInt64LE(num.toShort())
public inline fun MutableList<Byte>.writeUInt64LE(num: UByte): Number? = writeUInt64LE(num.toByte())

public inline fun MutableList<Byte>.writeUInt64BE(num: ULong): Number? = writeUInt64BE(num.toLong())
public inline fun MutableList<Byte>.writeUInt64BE(num: UInt): Number? = writeUInt64BE(num.toInt())
public inline fun MutableList<Byte>.writeUInt64BE(num: UShort): Number? = writeUInt64BE(num.toShort())
public inline fun MutableList<Byte>.writeUInt64BE(num: UByte): Number? = writeUInt64BE(num.toByte())

public inline fun MutableList<Byte>.writeUInt64LE(num: Number): Number? = writeInt64LE(num)
public inline fun MutableList<Byte>.writeUInt64BE(num: Number): Number? = writeInt64BE(num)

public fun MutableList<Byte>.writeInt56LE(num: Number): Number? {
    if (size < 7)
        return null

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
public fun MutableList<Byte>.writeInt56BE(num: Number): Number? {
    if (size < 7)
        return null

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

public fun MutableList<Byte>.writeInt48LE(num: Number): Number? {
    if (size < 6)
        return null

    val long = num.toLong()

    this[0] = long.asByte(0)
    this[1] = long.asByte(8)
    this[2] = long.asByte(16)
    this[3] = long.asByte(24)
    this[4] = long.asByte(32)
    this[5] = long.asByte(40)

    return long
}
public fun MutableList<Byte>.writeInt48BE(num: Number): Number? {
    if (size < 6)
        return null

    val long = num.toLong()

    this[0] = long.asByte(40)
    this[1] = long.asByte(32)
    this[2] = long.asByte(24)
    this[3] = long.asByte(16)
    this[4] = long.asByte(8)
    this[5] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.writeInt40LE(num: Number): Number? {
    if (size < 5)
        return null

    val long = num.toLong()

    this[0] = long.asByte(0)
    this[1] = long.asByte(8)
    this[2] = long.asByte(16)
    this[3] = long.asByte(24)
    this[4] = long.asByte(32)

    return long
}
public fun MutableList<Byte>.writeInt40BE(num: Number): Number? {
    if (size < 5)
        return null

    val long = num.toLong()

    this[0] = long.asByte(32)
    this[1] = long.asByte(24)
    this[2] = long.asByte(16)
    this[3] = long.asByte(8)
    this[4] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.writeInt32LE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    this[0] = int.asByte(0)
    this[1] = int.asByte(8)
    this[2] = int.asByte(16)
    this[3] = int.asByte(24)

    return int
}
public fun MutableList<Byte>.writeInt32BE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    this[0] = int.asByte(24)
    this[1] = int.asByte(16)
    this[2] = int.asByte(8)
    this[3] = int.asByte(0)

    return int
}

public inline fun MutableList<Byte>.writeUInt32LE(num: ULong): Number? = writeUInt32LE(num.toLong())
public inline fun MutableList<Byte>.writeUInt32LE(num: UInt): Number? = writeUInt32LE(num.toInt())
public inline fun MutableList<Byte>.writeUInt32LE(num: UShort): Number? = writeUInt32LE(num.toShort())
public inline fun MutableList<Byte>.writeUInt32LE(num: UByte): Number? = writeUInt32LE(num.toByte())

public inline fun MutableList<Byte>.writeUInt32BE(num: ULong): Number? = writeUInt32LE(num.toLong())
public inline fun MutableList<Byte>.writeUInt32BE(num: UInt): Number? = writeUInt32LE(num.toInt())
public inline fun MutableList<Byte>.writeUInt32BE(num: UShort): Number? = writeUInt32LE(num.toShort())
public inline fun MutableList<Byte>.writeUInt32BE(num: UByte): Number? = writeUInt32LE(num.toByte())


public inline fun MutableList<Byte>.writeUInt32LE(num: Number): Number? = writeInt32LE(num)
public inline fun MutableList<Byte>.writeUInt32BE(num: Number): Number? = writeInt32BE(num)

public fun MutableList<Byte>.writeInt24LE(num: Number): Number? {
    if (size < 3)
        return null

    val word = num.toInt() and 0xFFFFFF

    this[0] = word.asByte(0)
    this[1] = word.asByte(8)
    this[2] = word.asByte(16)

    return word
}

public fun MutableList<Byte>.writeInt24BE(num: Number): Number? {
    if (size < 3)
        return null

    val word = num.toInt() and 0xFFFFFF

    this[0] = word.asByte(16)
    this[1] = word.asByte(8)
    this[2] = word.asByte(0)

    return word
}

public fun MutableList<Byte>.writeInt16LE(num: Number): Number? {
    if (size < 2)
        return null

    val short = num.toShort()

    this[0] = short.asByte(0)
    this[1] = short.asByte(8)

    return short
}
public fun MutableList<Byte>.writeInt16BE(num: Number): Number? {
    if (size < 2)
        return null

    val short = num.toShort()

    this[0] = short.asByte(8)
    this[1] = short.asByte(0)

    return short
}

public fun MutableList<Byte>.writeVariableInt16(num: Number): Number {
    val short = num.toShort()

    if (short < 0x80) {
        this[0] = short.asByte()
    } else {
        this[0] = short.or(0x80).asByte()
        this[1] = short.asByte(7)
    }

    return short
}

public inline fun MutableList<Byte>.writeFloatBE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun MutableList<Byte>.writeFloatLE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun MutableList<Byte>.writeFloat32BE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun MutableList<Byte>.writeFloat32LE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun MutableList<Byte>.writeDoubleBE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun MutableList<Byte>.writeDoubleLE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())
public inline fun MutableList<Byte>.writeFloat64BE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun MutableList<Byte>.writeFloat64LE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())


/** Read from Index */


public fun MutableList<Byte>.writeInt64LE(index: Int, num: Number): Number? {
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
public fun MutableList<Byte>.writeInt64BE(index: Int, num: Number): Number? {
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

public inline fun MutableList<Byte>.writeUInt64LE(index: Int, num: ULong): Number? = writeInt64LE(index, num.toLong())
public inline fun MutableList<Byte>.writeUInt64LE(index: Int, num: UInt): Number? = writeInt64LE(index, num.toInt())
public inline fun MutableList<Byte>.writeUInt64LE(index: Int, num: UShort): Number? = writeInt64LE(index, num.toShort())
public inline fun MutableList<Byte>.writeUInt64LE(index: Int, num: UByte): Number? = writeInt64LE(index, num.toByte())
public inline fun MutableList<Byte>.writeUInt64LE(index: Int, num: Number): Number? = writeInt64LE(index, num)
public inline fun MutableList<Byte>.writeUInt64BE(index: Int, num: ULong): Number? = writeInt64BE(index, num.toLong())
public inline fun MutableList<Byte>.writeUInt64BE(index: Int, num: UInt): Number? = writeInt64BE(index, num.toInt())
public inline fun MutableList<Byte>.writeUInt64BE(index: Int, num: UShort): Number? = writeInt64BE(index, num.toShort())
public inline fun MutableList<Byte>.writeUInt64BE(index: Int, num: UByte): Number? = writeInt64BE(index, num.toByte())
public inline fun MutableList<Byte>.writeUInt64BE(index: Int, num: Number): Number? = writeInt64BE(index, num)

public fun MutableList<Byte>.writeInt56LE(index: Int, num: Number): Number? {
    if (size < 7)
        return null

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
public fun MutableList<Byte>.writeInt56BE(index: Int, num: Number): Number? {
    if (size < 7)
        return null

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

public fun MutableList<Byte>.writeInt48LE(index: Int, num: Number): Number? {
    if (size < 6)
        return null

    val long = num.toLong()

    this[index + 0] = long.asByte(0)
    this[index + 1] = long.asByte(8)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(32)
    this[index + 5] = long.asByte(40)

    return long
}
public fun MutableList<Byte>.writeInt48BE(index: Int, num: Number): Number? {
    if (size < 6)
        return null

    val long = num.toLong()

    this[index + 0] = long.asByte(40)
    this[index + 1] = long.asByte(32)
    this[index + 2] = long.asByte(24)
    this[index + 3] = long.asByte(16)
    this[index + 4] = long.asByte(8)
    this[index + 5] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.writeInt40LE(index: Int, num: Number): Number? {
    if (size < 5)
        return null

    val long = num.toLong()

    this[index + 0] = long.asByte(0)
    this[index + 1] = long.asByte(8)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(24)
    this[index + 4] = long.asByte(32)

    return long
}
public fun MutableList<Byte>.writeInt40BE(index: Int, num: Number): Number? {
    if (size < 5)
        return null

    val long = num.toLong()

    this[index + 0] = long.asByte(32)
    this[index + 1] = long.asByte(24)
    this[index + 2] = long.asByte(16)
    this[index + 3] = long.asByte(8)
    this[index + 4] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.writeInt32LE(index: Int, num: Number): Number? {
    if (size - 4 < index)
        return null

    val int = num.toInt()

    this[index + 0] = int.asByte(0)
    this[index + 1] = int.asByte(8)
    this[index + 2] = int.asByte(16)
    this[index + 3] = int.asByte(24)

    return int
}
public fun MutableList<Byte>.writeInt32BE(index: Int, num: Number): Number? {
    if (size - 4 < index)
        return null

    val int = num.toInt()

    this[index + 0] = int.asByte(24)
    this[index + 1] = int.asByte(16)
    this[index + 2] = int.asByte(8)
    this[index + 3] = int.asByte(0)

    return int
}

public inline fun MutableList<Byte>.writeUInt32LE(index: Int, num: ULong): Number? = writeUInt32LE(index, num.toLong())
public inline fun MutableList<Byte>.writeUInt32LE(index: Int, num: UInt): Number? = writeUInt32LE(index, num.toInt())
public inline fun MutableList<Byte>.writeUInt32LE(index: Int, num: UShort): Number? = writeUInt32LE(index, num.toShort())
public inline fun MutableList<Byte>.writeUInt32LE(index: Int, num: UByte): Number? = writeUInt32LE(index, num.toByte())
public inline fun MutableList<Byte>.writeUInt32LE(index: Int, num: Number): Number? = writeInt32LE(index, num)

public inline fun MutableList<Byte>.writeUInt32BE(index: Int, num: ULong): Number? = writeUInt32BE(index, num.toLong())
public inline fun MutableList<Byte>.writeUInt32BE(index: Int, num: UInt): Number? = writeUInt32BE(index, num.toInt())
public inline fun MutableList<Byte>.writeUInt32BE(index: Int, num: UShort): Number? = writeUInt32BE(index, num.toShort())
public inline fun MutableList<Byte>.writeUInt32BE(index: Int, num: UByte): Number? = writeUInt32BE(index, num.toByte())
public inline fun MutableList<Byte>.writeUInt32BE(index: Int, num: Number): Number? = writeInt32BE(index, num)

public fun MutableList<Byte>.writeInt24LE(index: Int, num: Number): Number? {
    if (size < 3)
        return null

    val word = num.toInt() and 0xFFFFFF

    this[index + 0] = word.asByte(0)
    this[index + 1] = word.asByte(8)
    this[index + 2] = word.asByte(16)

    return word
}

public fun MutableList<Byte>.writeInt24BE(index: Int, num: Number): Number? {
    if (size < 3)
        return null

    val word = num.toInt() and 0xFFFFFF

    this[index + 0] = word.asByte(16)
    this[index + 1] = word.asByte(8)
    this[index + 2] = word.asByte(0)

    return word
}

public fun MutableList<Byte>.writeInt16LE(index: Int, num: Number): Number? {
    if (size - 2 < index)
        return null

    val short = num.toShort()

    this[index + 0] = short.asByte(0)
    this[index + 1] = short.asByte(8)

    return short
}
public fun MutableList<Byte>.writeInt16BE(index: Int, num: Number): Number? {
    if (size - 2 < index)
        return null

    val short = num.toShort()

    this[index + 0] = short.asByte(0)
    this[index + 1] = short.asByte(8)

    return short
}

public fun MutableList<Byte>.writeVariableInt16(index: Int, num: Number): Number? {
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

public inline fun MutableList<Byte>.writeFloatBE(index: Int, num: Number): Number? = this.writeInt32BE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.writeFloatLE(index: Int, num: Number): Number? = this.writeInt32LE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.writeFloat32BE(index: Int, num: Number): Number? = this.writeInt32BE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.writeFloat32LE(index: Int, num: Number): Number? = this.writeInt32LE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.writeDoubleBE(index: Int, num: Number): Number? = this.writeInt64BE(index, num.toDouble().toBits())
public inline fun MutableList<Byte>.writeDoubleLE(index: Int, num: Number): Number? = this.writeInt64LE(index, num.toDouble().toBits())
public inline fun MutableList<Byte>.writeFloat64BE(index: Int, num: Number): Number? = this.writeInt64BE(index, num.toDouble().toBits())
public inline fun MutableList<Byte>.writeFloat64LE(index: Int, num: Number): Number? = this.writeInt64LE(index, num.toDouble().toBits())


//* * * * * * * * 
//* Add To List *
//* * * * * * * *


public fun MutableList<Byte>.addInt64LE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(0))
    add(long.asByte(8))
    add(long.asByte(16))
    add(long.asByte(24))
    add(long.asByte(32))
    add(long.asByte(40))
    add(long.asByte(48))
    add(long.asByte(56))

    return long
}
public fun MutableList<Byte>.addInt64BE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(56))
    add(long.asByte(48))
    add(long.asByte(40))
    add(long.asByte(32))
    add(long.asByte(24))
    add(long.asByte(16))
    add(long.asByte(8))
    add(long.asByte(0))

    return long
}

public inline fun MutableList<Byte>.addUInt64LE(num: ULong): Number = addUInt64LE(num.toLong())
public inline fun MutableList<Byte>.addUInt64LE(num: UInt): Number = addUInt64LE(num.toInt())
public inline fun MutableList<Byte>.addUInt64LE(num: UShort): Number = addUInt64LE(num.toShort())
public inline fun MutableList<Byte>.addUInt64LE(num: UByte): Number = addUInt64LE(num.toByte())

public inline fun MutableList<Byte>.addUInt64BE(num: ULong): Number = addUInt64BE(num.toLong())
public inline fun MutableList<Byte>.addUInt64BE(num: UInt): Number = addUInt64BE(num.toInt())
public inline fun MutableList<Byte>.addUInt64BE(num: UShort): Number = addUInt64BE(num.toShort())
public inline fun MutableList<Byte>.addUInt64BE(num: UByte): Number = addUInt64BE(num.toByte())

public inline fun MutableList<Byte>.addUInt64LE(num: Number): Number = addInt64LE(num)
public inline fun MutableList<Byte>.addUInt64BE(num: Number): Number = addInt64BE(num)

public fun MutableList<Byte>.addInt56LE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(0)) //this[0] = long.asByte(0)
    add(long.asByte(8)) //this[1] = long.asByte(8)
    add(long.asByte(16)) //this[2] = long.asByte(16)
    add(long.asByte(24)) //this[3] = long.asByte(24)
    add(long.asByte(32)) //this[4] = long.asByte(32)
    add(long.asByte(40)) //this[5] = long.asByte(40)
    add(long.asByte(48)) //this[6] = long.asByte(48)

    return long
}
public fun MutableList<Byte>.addInt56BE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(48)) //this[0] = long.asByte(48)
    add(long.asByte(40)) //this[1] = long.asByte(40)
    add(long.asByte(32)) //this[2] = long.asByte(32)
    add(long.asByte(24)) //this[3] = long.asByte(24)
    add(long.asByte(16)) //this[4] = long.asByte(16)
    add(long.asByte(8)) //this[5] = long.asByte(8)
    add(long.asByte(0)) //this[6] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.addInt48LE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(0)) //this[0] = long.asByte(0)
    add(long.asByte(8)) //this[1] = long.asByte(8)
    add(long.asByte(16)) //this[2] = long.asByte(16)
    add(long.asByte(24)) //this[3] = long.asByte(24)
    add(long.asByte(32)) //this[4] = long.asByte(32)
    add(long.asByte(40)) //this[5] = long.asByte(40)

    return long
}
public fun MutableList<Byte>.addInt48BE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(40)) //this[0] = long.asByte(40)
    add(long.asByte(32)) //this[1] = long.asByte(32)
    add(long.asByte(24)) //this[2] = long.asByte(24)
    add(long.asByte(16)) //this[3] = long.asByte(16)
    add(long.asByte(8)) //this[4] = long.asByte(8)
    add(long.asByte(0)) //this[5] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.addInt40LE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(0)) //this[0] = long.asByte(0)
    add(long.asByte(8)) //this[1] = long.asByte(8)
    add(long.asByte(16)) //this[2] = long.asByte(16)
    add(long.asByte(24)) //this[3] = long.asByte(24)
    add(long.asByte(32)) //this[4] = long.asByte(32)

    return long
}
public fun MutableList<Byte>.addInt40BE(num: Number): Number {
    val long = num.toLong()

    add(long.asByte(32)) //this[0] = long.asByte(32)
    add(long.asByte(24)) //this[1] = long.asByte(24)
    add(long.asByte(16)) //this[2] = long.asByte(16)
    add(long.asByte(8)) //this[3] = long.asByte(8)
    add(long.asByte(0)) //this[4] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.addInt32LE(num: Number): Number {
    val int = num.toInt()

    add(int.asByte(0)) //this[0] = int.asByte(0)
    add(int.asByte(8)) //this[1] = int.asByte(8)
    add(int.asByte(16)) //this[2] = int.asByte(16)
    add(int.asByte(24)) //this[3] = int.asByte(24)

    return int
}
public fun MutableList<Byte>.addInt32BE(num: Number): Number {
    val int = num.toInt()

    add(int.asByte(24)) //this[0] = int.asByte(24)
    add(int.asByte(16)) //this[1] = int.asByte(16)
    add(int.asByte(8)) //this[2] = int.asByte(8)
    add(int.asByte(0)) //this[3] = int.asByte(0)

    return int
}

public inline fun MutableList<Byte>.addUInt32LE(num: ULong): Number = addUInt32LE(num.toLong())
public inline fun MutableList<Byte>.addUInt32LE(num: UInt): Number = addUInt32LE(num.toInt())
public inline fun MutableList<Byte>.addUInt32LE(num: UShort): Number = addUInt32LE(num.toShort())
public inline fun MutableList<Byte>.addUInt32LE(num: UByte): Number = addUInt32LE(num.toByte())

public inline fun MutableList<Byte>.addUInt32BE(num: ULong): Number = addUInt32LE(num.toLong())
public inline fun MutableList<Byte>.addUInt32BE(num: UInt): Number = addUInt32LE(num.toInt())
public inline fun MutableList<Byte>.addUInt32BE(num: UShort): Number = addUInt32LE(num.toShort())
public inline fun MutableList<Byte>.addUInt32BE(num: UByte): Number = addUInt32LE(num.toByte())


public inline fun MutableList<Byte>.addUInt32LE(num: Number): Number = addInt32LE(num)
public inline fun MutableList<Byte>.addUInt32BE(num: Number): Number = addInt32BE(num)

public fun MutableList<Byte>.addInt24LE(num: Number): Number {
    val word = num.toInt() and 0xFFFFFF

    add(word.asByte(0)) //this[0] = word.asByte(0)
    add(word.asByte(8)) //this[1] = word.asByte(8)
    add(word.asByte(16)) //this[2] = word.asByte(16)

    return word
}

public fun MutableList<Byte>.addInt24BE(num: Number): Number {
    val word = num.toInt() and 0xFFFFFF

    add(word.asByte(16)) //this[0] = word.asByte(16)
    add(word.asByte(8)) //this[1] = word.asByte(8)
    add(word.asByte(0)) //this[2] = word.asByte(0)

    return word
}

public fun MutableList<Byte>.addInt16LE(num: Number): Number {
    val short = num.toShort()

    add(short.asByte(0)) //this[0] = short.asByte(0)
    add(short.asByte(8)) //this[1] = short.asByte(8)

    return short
}
public fun MutableList<Byte>.addInt16BE(num: Number): Number {
    val short = num.toShort()

    add(short.asByte(8)) //this[0] = short.asByte(8)
    add(short.asByte(0)) //this[1] = short.asByte(0)

    return short
}

public fun MutableList<Byte>.addVariableInt16(num: Number): Number {
    val short = num.toShort()

    if (short < 0x80) {
        add(short.asByte()) //this[0] = short.asByte()
    } else {
        add(short.or(0x80).asByte()) //this[0] = short.or(0x80).asByte()
        add(short.asByte(7)) //this[1] = short.asByte(7)
    }

    return short
}

public inline fun MutableList<Byte>.addFloatBE(num: Number): Number = this.addInt32BE(num.toFloat().toBits())
public inline fun MutableList<Byte>.addFloatLE(num: Number): Number = this.addInt32LE(num.toFloat().toBits())
public inline fun MutableList<Byte>.addFloat32BE(num: Number): Number = this.addInt32BE(num.toFloat().toBits())
public inline fun MutableList<Byte>.addFloat32LE(num: Number): Number = this.addInt32LE(num.toFloat().toBits())
public inline fun MutableList<Byte>.addDoubleBE(num: Number): Number = this.addInt64BE(num.toDouble().toBits())
public inline fun MutableList<Byte>.addDoubleLE(num: Number): Number = this.addInt64LE(num.toDouble().toBits())
public inline fun MutableList<Byte>.addFloat64BE(num: Number): Number = this.addInt64BE(num.toDouble().toBits())
public inline fun MutableList<Byte>.addFloat64LE(num: Number): Number = this.addInt64LE(num.toDouble().toBits())


/** Read from Index */


public fun MutableList<Byte>.addInt64LE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(0)) //this[index + 0] = long.asByte(0)
    add(index + 1, long.asByte(8)) //this[index + 1] = long.asByte(8)
    add(index + 2, long.asByte(16)) //this[index + 2] = long.asByte(16)
    add(index + 3, long.asByte(24)) //this[index + 3] = long.asByte(24)
    add(index + 4, long.asByte(32)) //this[index + 4] = long.asByte(32)
    add(index + 5, long.asByte(40)) //this[index + 5] = long.asByte(40)
    add(index + 6, long.asByte(48)) //this[index + 6] = long.asByte(48)
    add(index + 7, long.asByte(56)) //this[index + 7] = long.asByte(56)

    return long
}
public fun MutableList<Byte>.addInt64BE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(56)) //this[index + 0] = long.asByte(56)
    add(index + 1, long.asByte(48)) //this[index + 1] = long.asByte(48)
    add(index + 2, long.asByte(40)) //this[index + 2] = long.asByte(40)
    add(index + 3, long.asByte(32)) //this[index + 3] = long.asByte(32)
    add(index + 4, long.asByte(24)) //this[index + 4] = long.asByte(24)
    add(index + 5, long.asByte(16)) //this[index + 5] = long.asByte(16)
    add(index + 6, long.asByte(8)) //this[index + 6] = long.asByte(8)
    add(index + 7, long.asByte(0)) //this[index + 7] = long.asByte(0)

    return long
}

public inline fun MutableList<Byte>.addUInt64LE(index: Int, num: ULong): Number = addInt64LE(index, num.toLong())
public inline fun MutableList<Byte>.addUInt64LE(index: Int, num: UInt): Number = addInt64LE(index, num.toInt())
public inline fun MutableList<Byte>.addUInt64LE(index: Int, num: UShort): Number = addInt64LE(index, num.toShort())
public inline fun MutableList<Byte>.addUInt64LE(index: Int, num: UByte): Number = addInt64LE(index, num.toByte())
public inline fun MutableList<Byte>.addUInt64LE(index: Int, num: Number): Number = addInt64LE(index, num)
public inline fun MutableList<Byte>.addUInt64BE(index: Int, num: ULong): Number = addInt64BE(index, num.toLong())
public inline fun MutableList<Byte>.addUInt64BE(index: Int, num: UInt): Number = addInt64BE(index, num.toInt())
public inline fun MutableList<Byte>.addUInt64BE(index: Int, num: UShort): Number = addInt64BE(index, num.toShort())
public inline fun MutableList<Byte>.addUInt64BE(index: Int, num: UByte): Number = addInt64BE(index, num.toByte())
public inline fun MutableList<Byte>.addUInt64BE(index: Int, num: Number): Number = addInt64BE(index, num)

public fun MutableList<Byte>.addInt56LE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(0)) //this[index + 0] = long.asByte(0)
    add(index + 1, long.asByte(8)) //this[index + 1] = long.asByte(8)
    add(index + 2, long.asByte(16)) //this[index + 2] = long.asByte(16)
    add(index + 3, long.asByte(24)) //this[index + 3] = long.asByte(24)
    add(index + 4, long.asByte(32)) //this[index + 4] = long.asByte(32)
    add(index + 5, long.asByte(40)) //this[index + 5] = long.asByte(40)
    add(index + 6, long.asByte(48)) //this[index + 6] = long.asByte(48)

    return long
}
public fun MutableList<Byte>.addInt56BE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(48)) //this[index + 0] = long.asByte(48)
    add(index + 1, long.asByte(40)) //this[index + 1] = long.asByte(40)
    add(index + 2, long.asByte(32)) //this[index + 2] = long.asByte(32)
    add(index + 3, long.asByte(24)) //this[index + 3] = long.asByte(24)
    add(index + 4, long.asByte(16)) //this[index + 4] = long.asByte(16)
    add(index + 5, long.asByte(8)) //this[index + 5] = long.asByte(8)
    add(index + 6, long.asByte(0)) //this[index + 6] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.addInt48LE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(0)) //this[index + 0] = long.asByte(0)
    add(index + 1, long.asByte(8)) //this[index + 1] = long.asByte(8)
    add(index + 2, long.asByte(16)) //this[index + 2] = long.asByte(16)
    add(index + 3, long.asByte(24)) //this[index + 3] = long.asByte(24)
    add(index + 4, long.asByte(32)) //this[index + 4] = long.asByte(32)
    add(index + 5, long.asByte(40)) //this[index + 5] = long.asByte(40)

    return long
}
public fun MutableList<Byte>.addInt48BE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(40)) //this[index + 0] = long.asByte(40)
    add(index + 1, long.asByte(32)) //this[index + 1] = long.asByte(32)
    add(index + 2, long.asByte(24)) //this[index + 2] = long.asByte(24)
    add(index + 3, long.asByte(16)) //this[index + 3] = long.asByte(16)
    add(index + 4, long.asByte(8)) //this[index + 4] = long.asByte(8)
    add(index + 5, long.asByte(0)) //this[index + 5] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.addInt40LE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(0)) //this[index + 0] = long.asByte(0)
    add(index + 1, long.asByte(8)) //this[index + 1] = long.asByte(8)
    add(index + 2, long.asByte(16)) //this[index + 2] = long.asByte(16)
    add(index + 3, long.asByte(24)) //this[index + 3] = long.asByte(24)
    add(index + 4, long.asByte(32)) //this[index + 4] = long.asByte(32)

    return long
}
public fun MutableList<Byte>.addInt40BE(index: Int, num: Number): Number {
    val long = num.toLong()

    add(index + 0, long.asByte(32)) //this[index + 0] = long.asByte(32)
    add(index + 1, long.asByte(24)) //this[index + 1] = long.asByte(24)
    add(index + 2, long.asByte(16)) //this[index + 2] = long.asByte(16)
    add(index + 3, long.asByte(8)) //this[index + 3] = long.asByte(8)
    add(index + 4, long.asByte(0)) //this[index + 4] = long.asByte(0)

    return long
}

public fun MutableList<Byte>.addInt32LE(index: Int, num: Number): Number {
    val int = num.toInt()

    add(index + 0, int.asByte(0)) //this[index + 0] = int.asByte(0)
    add(index + 1, int.asByte(8)) //this[index + 1] = int.asByte(8)
    add(index + 2, int.asByte(16)) //this[index + 2] = int.asByte(16)
    add(index + 3, int.asByte(24)) //this[index + 3] = int.asByte(24)

    return int
}
public fun MutableList<Byte>.addInt32BE(index: Int, num: Number): Number {
    val int = num.toInt()

    add(index + 0, int.asByte(24)) //this[index + 0] = int.asByte(24)
    add(index + 1, int.asByte(16)) //this[index + 1] = int.asByte(16)
    add(index + 2, int.asByte(8)) //this[index + 2] = int.asByte(8)
    add(index + 3, int.asByte(0)) //this[index + 3] = int.asByte(0)

    return int
}

public inline fun MutableList<Byte>.addUInt32LE(index: Int, num: ULong): Number = addUInt32LE(index, num.toLong())
public inline fun MutableList<Byte>.addUInt32LE(index: Int, num: UInt): Number = addUInt32LE(index, num.toInt())
public inline fun MutableList<Byte>.addUInt32LE(index: Int, num: UShort): Number = addUInt32LE(index, num.toShort())
public inline fun MutableList<Byte>.addUInt32LE(index: Int, num: UByte): Number = addUInt32LE(index, num.toByte())
public inline fun MutableList<Byte>.addUInt32LE(index: Int, num: Number): Number = addInt32LE(index, num)

public inline fun MutableList<Byte>.addUInt32BE(index: Int, num: ULong): Number = addUInt32BE(index, num.toLong())
public inline fun MutableList<Byte>.addUInt32BE(index: Int, num: UInt): Number = addUInt32BE(index, num.toInt())
public inline fun MutableList<Byte>.addUInt32BE(index: Int, num: UShort): Number = addUInt32BE(index, num.toShort())
public inline fun MutableList<Byte>.addUInt32BE(index: Int, num: UByte): Number = addUInt32BE(index, num.toByte())
public inline fun MutableList<Byte>.addUInt32BE(index: Int, num: Number): Number = addInt32BE(index, num)

public fun MutableList<Byte>.addInt24LE(index: Int, num: Number): Number {
    val word = num.toInt() and 0xFFFFFF

    add(index + 0, word.asByte(0)) //this[index + 0] = word.asByte(0)
    add(index + 1, word.asByte(8)) //this[index + 1] = word.asByte(8)
    add(index + 2, word.asByte(16)) //this[index + 2] = word.asByte(16)

    return word
}

public fun MutableList<Byte>.addInt24BE(index: Int, num: Number): Number {
    val word = num.toInt() and 0xFFFFFF

    add(index + 0, word.asByte(16)) //this[index + 0] = word.asByte(16)
    add(index + 1, word.asByte(8)) //this[index + 1] = word.asByte(8)
    add(index + 2, word.asByte(0)) //this[index + 2] = word.asByte(0)

    return word
}

public fun MutableList<Byte>.addInt16LE(index: Int, num: Number): Number {
    val short = num.toShort()

    add(index + 0, short.asByte(0)) //this[index + 0] = short.asByte(0)
    add(index + 1, short.asByte(8)) //this[index + 1] = short.asByte(8)

    return short
}
public fun MutableList<Byte>.addInt16BE(index: Int, num: Number): Number {
    val short = num.toShort()

    add(index + 0, short.asByte(0)) //this[index + 0] = short.asByte(0)
    add(index + 1, short.asByte(8)) //this[index + 1] = short.asByte(8)

    return short
}

public fun MutableList<Byte>.addVariableInt16(index: Int, num: Number): Number {
    val short = num.toShort()

    if (short < 0x80) {
        add(index + 0, short.asByte()) //this[index] = short.asByte()
    } else {
        add(index + 0, short.or(0x80).asByte()) //this[index] = short.or(0x80).asByte()
        add(index + 1, short.asByte(7)) //this[index + 1] = short.asByte(7)
    }

    return short
}

public inline fun MutableList<Byte>.addFloatBE(index: Int, num: Number): Number = this.addInt32BE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.addFloatLE(index: Int, num: Number): Number = this.addInt32LE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.addFloat32BE(index: Int, num: Number): Number = this.addInt32BE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.addFloat32LE(index: Int, num: Number): Number = this.addInt32LE(index, num.toFloat().toBits())
public inline fun MutableList<Byte>.addDoubleBE(index: Int, num: Number): Number = this.addInt64BE(index, num.toDouble().toBits())
public inline fun MutableList<Byte>.addDoubleLE(index: Int, num: Number): Number = this.addInt64LE(index, num.toDouble().toBits())
public inline fun MutableList<Byte>.addFloat64BE(index: Int, num: Number): Number = this.addInt64BE(index, num.toDouble().toBits())
public inline fun MutableList<Byte>.addFloat64LE(index: Int, num: Number): Number = this.addInt64LE(index, num.toDouble().toBits())

public inline fun MutableList<Byte>.add(byte: Int): Boolean = add(byte.toByte())

public inline fun buildBinaryData(builder: MutableList<Byte>.() -> Unit): ByteArray =
    ArrayList<Byte>().apply(builder).toByteArray()