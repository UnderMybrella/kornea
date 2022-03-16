@file:Suppress("DuplicatedCode")

package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.io.common.*
import dev.brella.kornea.toolkit.common.*

/** Read from base */

public fun Int64Packet.readInt64LE(): Long? {
    if (size < 8)
        return null

    return h.asLong(56) or
            g.asLong(48) or
            f.asLong(40) or
            e.asLong(32) or
            d.asLong(24) or
            c.asLong(16) or
            b.asLong(8) or
            a.asLong(0)
}

public fun Int64Packet.readInt64BE(): Long? {
    if (size < 8)
        return null

    return a.asLong(56) or
            b.asLong(48) or
            c.asLong(40) or
            d.asLong(32) or
            e.asLong(24) or
            f.asLong(16) or
            g.asLong(8) or
            h.asLong(0)
}

public fun Int64Packet.readUInt64LE(): ULong? {
    if (size < 8)
        return null

    return h.asULong(56) or
            g.asULong(48) or
            f.asULong(40) or
            e.asULong(32) or
            d.asULong(24) or
            c.asULong(16) or
            b.asULong(8) or
            a.asULong(0)
}

public fun Int64Packet.readUInt64BE(): ULong? {
    if (size < 8)
        return null

    return a.asULong(56) or
            b.asULong(48) or
            c.asULong(40) or
            d.asULong(32) or
            e.asULong(24) or
            f.asULong(16) or
            g.asULong(8) or
            h.asULong(0)
}

public fun Int56Packet.readInt56LE(): Int? {
    if (size < 7)
        return null

    return g.asInt(48) or 
            f.asInt(40) or 
            e.asInt(32) or
            d.asInt(24) or
            c.asInt(16) or
            b.asInt(8) or
            a.asInt(0)
}

public fun Int56Packet.readInt56BE(): Int? {
    if (size < 7)
        return null

    return a.asInt(48) or
            b.asInt(40) or
            c.asInt(32) or
            d.asInt(24) or
            e.asInt(16) or 
            f.asInt(8) or 
            g.asInt()
}

public fun Int48Packet.readInt48LE(): Int? {
    if (size < 6)
        return null

    return f.asInt(40) or
            e.asInt(32) or
            d.asInt(24) or
            c.asInt(16) or
            b.asInt(8) or
            a.asInt(0)
}

public fun Int48Packet.readInt48BE(): Int? {
    if (size < 6)
        return null

    return a.asInt(40) or
            b.asInt(32) or
            c.asInt(24) or
            d.asInt(16) or 
            e.asInt(8) or 
            f.asInt(0)
}

public fun Int40Packet.readInt40LE(): Int? {
    if (size < 5)
        return null

    return e.asInt(32) or 
            d.asInt(24) or
            c.asInt(16) or
            b.asInt(8) or
            a.asInt(0)
}

public fun Int40Packet.readInt40BE(): Int? {
    if (size < 5)
        return null

    return a.asInt(32) or
            b.asInt(24) or
            c.asInt(16) or
            d.asInt(8) or 
            e.asInt(0)
}

public fun Int32Packet.readInt32LE(): Int? {
    if (size < 4)
        return null

    return d.asInt(24) or
            c.asInt(16) or
            b.asInt(8) or
            a.asInt(0)
}

public fun Int32Packet.readInt32BE(): Int? {
    if (size < 4)
        return null

    return a.asInt(24) or
            b.asInt(16) or
            c.asInt(8) or
            d.asInt(0)
}

public fun Int32Packet.readUInt32LE(): UInt? {
    if (size < 4)
        return null

    return d.asUInt(24) or
            c.asUInt(16) or
            b.asUInt(8) or
            a.asUInt(0)
}

public fun Int32Packet.readUInt32BE(): UInt? {
    if (size < 4)
        return null

    return a.asUInt(24) or
            b.asUInt(16) or
            c.asUInt(8) or
            d.asUInt(0)
}

public fun Int24Packet.readInt24LE(): Int? {
    if (size < 3) return null

    return c.asInt(16) or
            b.asInt(8) or
            a.asInt()
}

public fun Int24Packet.readInt24BE(): Int? {
    if (size < 3) return null

    return a.asInt(16) or
            b.asInt(8) or
            c.asInt()
}

public fun Int16Packet.readInt16LE(): Int? {
    if (size < 2)
        return null

    return b.asInt(8) or a.asInt(0)
}

public fun Int16Packet.readInt16BE(): Int? {
    if (size < 2)
        return null

    return a.asInt(8) or b.asInt(0)
}

public inline fun Int32Packet.readFloatBE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun Int32Packet.readFloatLE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun Int32Packet.readFloat32BE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }
public inline fun Int32Packet.readFloat32LE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }
public inline fun Int64Packet.readDoubleBE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun Int64Packet.readDoubleLE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }
public inline fun Int64Packet.readFloat64BE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }
public inline fun Int64Packet.readFloat64LE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }

/** Write at base */

public fun Int64Packet.writeInt64LE(num: Number): Number? {
    if (size < 8)
        return null

    val long = num.toLong()

    a = long.asByte(0)
    b = long.asByte(8)
    c = long.asByte(16)
    d = long.asByte(24)
    e = long.asByte(32)
    f = long.asByte(40)
    g = long.asByte(48)
    h = long.asByte(56)

    return long
}

public fun Int64Packet.writeInt64BE(num: Number): Number? {
    if (size < 8)
        return null

    val long = num.toLong()

    a = long.asByte(56)
    b = long.asByte(48)
    c = long.asByte(40)
    d = long.asByte(32)
    e = long.asByte(24)
    f = long.asByte(16)
    g = long.asByte(8)
    h = long.asByte(0)

    return long
}

public inline fun Int64Packet.writeUInt64LE(num: ULong): Number? = writeUInt64LE(num.toLong())

public inline fun Int64Packet.writeUInt64LE(num: UInt): Number? = writeUInt64LE(num.toInt())

public inline fun Int64Packet.writeUInt64LE(num: UShort): Number? = writeUInt64LE(num.toShort())

public inline fun Int64Packet.writeUInt64LE(num: UByte): Number? = writeUInt64LE(num.toByte())

public inline fun Int64Packet.writeUInt64BE(num: ULong): Number? = writeUInt64BE(num.toLong())

public inline fun Int64Packet.writeUInt64BE(num: UInt): Number? = writeUInt64BE(num.toInt())

public inline fun Int64Packet.writeUInt64BE(num: UShort): Number? = writeUInt64BE(num.toShort())

public inline fun Int64Packet.writeUInt64BE(num: UByte): Number? = writeUInt64BE(num.toByte())

public inline fun Int64Packet.writeUInt64LE(num: Number): Number? = writeInt64LE(num)
public inline fun Int64Packet.writeUInt64BE(num: Number): Number? = writeInt64BE(num)

public fun Int56Packet.writeInt56LE(num: Number): Number? {
    if (size < 7)
        return null

    val long = num.toLong() and 0xFFFFFFFFFFFFFF

    a = long.asByte(0)
    b = long.asByte(8)
    c = long.asByte(16)
    d = long.asByte(24)
    e = long.asByte(32)
    f = long.asByte(40)
    g = long.asByte(48)

    return long
}

public fun Int56Packet.writeInt56BE(num: Number): Number? {
    if (size < 7)
        return null

    val long = num.toLong() and 0xFFFFFFFFFFFFFF

    a = long.asByte(48)
    b = long.asByte(40)
    c = long.asByte(32)
    d = long.asByte(24)
    e = long.asByte(16)
    f = long.asByte(8)
    g = long.asByte(0)

    return long
}

public fun Int48Packet.writeInt48LE(num: Number): Number? {
    if (size < 6)
        return null

    val long = num.toLong() and 0xFFFFFFFFFFFF

    a = long.asByte(0)
    b = long.asByte(8)
    c = long.asByte(16)
    d = long.asByte(24)
    e = long.asByte(32)
    f = long.asByte(40)

    return long
}

public fun Int48Packet.writeInt48BE(num: Number): Number? {
    if (size < 6)
        return null

    val long = num.toLong() and 0xFFFFFFFFFFFF

    a = long.asByte(40)
    b = long.asByte(32)
    c = long.asByte(24)
    d = long.asByte(16)
    e = long.asByte(8)
    f = long.asByte()

    return long
}

public fun Int40Packet.writeInt40LE(num: Number): Number? {
    if (size < 5)
        return null

    val long = num.toLong() and 0xFFFFFFFFFF

    a = long.asByte(0)
    b = long.asByte(8)
    c = long.asByte(16)
    d = long.asByte(24)
    e = long.asByte(32)

    return long
}

public fun Int40Packet.writeInt40BE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    a = int.asByte(32)
    b = int.asByte(24)
    c = int.asByte(16)
    d = int.asByte(8)
    e = int.asByte(0)

    return int
}

public fun Int32Packet.writeInt32LE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    a = int.asByte(0)
    b = int.asByte(8)
    c = int.asByte(16)
    d = int.asByte(24)

    return int
}

public fun Int32Packet.writeInt32BE(num: Number): Number? {
    if (size < 4)
        return null

    val int = num.toInt()

    a = int.asByte(24)
    b = int.asByte(16)
    c = int.asByte(8)
    d = int.asByte(0)

    return int
}

public inline fun Int32Packet.writeUInt32LE(num: ULong): Number? = writeUInt32LE(num.toLong())

public inline fun Int32Packet.writeUInt32LE(num: UInt): Number? = writeUInt32LE(num.toInt())

public inline fun Int32Packet.writeUInt32LE(num: UShort): Number? = writeUInt32LE(num.toShort())

public inline fun Int32Packet.writeUInt32LE(num: UByte): Number? = writeUInt32LE(num.toByte())

public inline fun Int32Packet.writeUInt32BE(num: ULong): Number? = writeUInt32LE(num.toLong())

public inline fun Int32Packet.writeUInt32BE(num: UInt): Number? = writeUInt32LE(num.toInt())

public inline fun Int32Packet.writeUInt32BE(num: UShort): Number? = writeUInt32LE(num.toShort())

public inline fun Int32Packet.writeUInt32BE(num: UByte): Number? = writeUInt32LE(num.toByte())


public inline fun Int32Packet.writeUInt32LE(num: Number): Number? = writeInt32LE(num)
public inline fun Int32Packet.writeUInt32BE(num: Number): Number? = writeInt32BE(num)

//public fun Int24Packet.writeInt24BE(num: Number): Number? {
//    if (size < 3)
//        return null
//
////    val word = num.to
//
//    a =
//
//    return a.asInt(16) or
//            b.asInt(8) or
//            c.asInt(0)
//}

public fun Int24Packet.writeInt24LE(num: Number): Number? {
    if (size < 2)
        return null

    val int = num.toInt() and 0xFFFFFF

    a = int.asByte(0)
    b = int.asByte(8)
    c = int.asByte(16)

    return int
}

public fun Int24Packet.writeInt24BE(num: Number): Number? {
    if (size < 2)
        return null

    val int = num.toInt() and 0xFFFFFF

    a = int.asByte(16)
    b = int.asByte(8)
    c = int.asByte(0)

    return int
}

public fun Int16Packet.writeInt16LE(num: Number): Number? {
    if (size < 2)
        return null

    val short = num.toShort()

    b = short.asByte(8)
    a = short.asByte(0)

    return short
}

public fun Int16Packet.writeInt16BE(num: Number): Number? {
    if (size < 2)
        return null

    val short = num.toShort()

    a = short.asByte(8)
    b = short.asByte(0)

    return short
}

public inline fun Int32Packet.writeFloatBE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun Int32Packet.writeFloatLE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun Int32Packet.writeFloat32BE(num: Number): Number? = this.writeInt32BE(num.toFloat().toBits())
public inline fun Int32Packet.writeFloat32LE(num: Number): Number? = this.writeInt32LE(num.toFloat().toBits())
public inline fun Int64Packet.writeDoubleBE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun Int64Packet.writeDoubleLE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())
public inline fun Int64Packet.writeFloat64BE(num: Number): Number? = this.writeInt64BE(num.toDouble().toBits())
public inline fun Int64Packet.writeFloat64LE(num: Number): Number? = this.writeInt64LE(num.toDouble().toBits())