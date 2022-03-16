package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.PeekableInputFlow

public suspend fun PeekableInputFlow.peekInt64LE(): Long? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return (h shl 56) or (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt64BE(): Long? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return (a shl 56) or (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun PeekableInputFlow.peekUInt64LE(): ULong? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return ((h shl 56) or (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a).toULong()
}

public suspend fun PeekableInputFlow.peekUInt64BE(): ULong? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return ((a shl 56) or (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h).toULong()
}

public suspend fun PeekableInputFlow.peekInt56LE(): Long? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null

    return (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt56BE(): Long? {
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun PeekableInputFlow.peekInt48LE(): Long? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null

    return (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt48BE(): Long? {
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun PeekableInputFlow.peekInt40LE(): Long? {
    val a = peek(1)?.toLong() ?: return null
    val b = peek(2)?.toLong() ?: return null
    val c = peek(3)?.toLong() ?: return null
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null

    return (e shl 32) or (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt40BE(): Long? {
    val d = peek(4)?.toLong() ?: return null
    val e = peek(5)?.toLong() ?: return null
    val f = peek(6)?.toLong() ?: return null
    val g = peek(7)?.toLong() ?: return null
    val h = peek(8)?.toLong() ?: return null

    return (d shl 32) or (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun PeekableInputFlow.peekInt32LE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null
    val c = peek(3) ?: return null
    val d = peek(4) ?: return null

    return (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt32BE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null
    val c = peek(3) ?: return null
    val d = peek(4) ?: return null

    return (a shl 24) or (b shl 16) or (c shl 8) or d
}

public suspend fun PeekableInputFlow.peekUInt32LE(): UInt? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null
    val c = peek(3) ?: return null
    val d = peek(4) ?: return null

    return ((d shl 24) or (c shl 16) or (b shl 8) or a).toUInt()
}

public suspend fun PeekableInputFlow.peekUInt32BE(): UInt? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null
    val c = peek(3) ?: return null
    val d = peek(4) ?: return null

    return ((a shl 24) or (b shl 16) or (c shl 8) or d).toUInt()
}

public suspend fun PeekableInputFlow.peekInt24LE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null
    val c = peek(3) ?: return null

    return (c shl 16) or (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt24BE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null
    val c = peek(3) ?: return null
    
    return (a shl 16) or (b shl 8) or c
}

public suspend fun PeekableInputFlow.peekInt16LE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null

    return (b shl 8) or a
}

public suspend fun PeekableInputFlow.peekInt16BE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null

    return (a shl 8) or b
}

public suspend fun PeekableInputFlow.peekUInt16LE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null

    return ((b shl 8) or a) and 0xFFFF
}

public suspend fun PeekableInputFlow.peekUInt16BE(): Int? {
    val a = peek(1) ?: return null
    val b = peek(2) ?: return null

    return ((a shl 8) or b) and 0xFFFF
}

//@ExperimentalUnsignedTypes
//public suspend inline fun Int64InputFlowState.readInt64LE(): Long? =
//    readPacket(int64Packet)?.readInt64LE()

public suspend fun InputFlow.readInt64LE(): Long? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return (h shl 56) or (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun InputFlow.readInt64BE(): Long? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return (a shl 56) or (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun InputFlow.readUInt64LE(): ULong? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return ((h shl 56) or (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a).toULong()
}

public suspend fun InputFlow.readUInt64BE(): ULong? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return ((a shl 56) or (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h).toULong()
}

public suspend fun InputFlow.readInt56LE(): Long? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null

    return (g shl 48) or (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun InputFlow.readInt56BE(): Long? {
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return (b shl 48) or (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun InputFlow.readInt48LE(): Long? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null

    return (f shl 40) or (e shl 32) or
            (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun InputFlow.readInt48BE(): Long? {
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return (c shl 40) or (d shl 32) or
            (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun InputFlow.readInt40LE(): Long? {
    val a = read()?.toLong() ?: return null
    val b = read()?.toLong() ?: return null
    val c = read()?.toLong() ?: return null
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null

    return (e shl 32) or (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun InputFlow.readInt40BE(): Long? {
    val d = read()?.toLong() ?: return null
    val e = read()?.toLong() ?: return null
    val f = read()?.toLong() ?: return null
    val g = read()?.toLong() ?: return null
    val h = read()?.toLong() ?: return null

    return (d shl 32) or (e shl 24) or (f shl 16) or (g shl 8) or h
}

public suspend fun InputFlow.readInt32LE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null
    val c = read() ?: return null
    val d = read() ?: return null

    return (d shl 24) or (c shl 16) or (b shl 8) or a
}

public suspend fun InputFlow.readInt32BE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null
    val c = read() ?: return null
    val d = read() ?: return null

    return (a shl 24) or (b shl 16) or (c shl 8) or d
}

public suspend fun InputFlow.readUInt32LE(): UInt? {
    val a = read() ?: return null
    val b = read() ?: return null
    val c = read() ?: return null
    val d = read() ?: return null

    return ((d shl 24) or (c shl 16) or (b shl 8) or a).toUInt()
}

public suspend fun InputFlow.readUInt32BE(): UInt? {
    val a = read() ?: return null
    val b = read() ?: return null
    val c = read() ?: return null
    val d = read() ?: return null

    return ((a shl 24) or (b shl 16) or (c shl 8) or d).toUInt()
}

public suspend fun InputFlow.readInt24LE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null
    val c = read() ?: return null

    return (c shl 16) or (b shl 8) or a
}

public suspend fun InputFlow.readInt24BE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null
    val c = read() ?: return null

    return (a shl 16) or (b shl 8) or c
}

public suspend fun InputFlow.readInt16LE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null

    return (b shl 8) or a
}

public suspend fun InputFlow.readInt16BE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null

    return (a shl 8) or b
}

public suspend fun InputFlow.readUInt16LE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null

    return ((b shl 8) or a) and 0xFFFF
}

public suspend fun InputFlow.readUInt16BE(): Int? {
    val a = read() ?: return null
    val b = read() ?: return null

    return ((a shl 8) or b) and 0xFFFF
}

public suspend fun InputFlow.readVariableInt16(): Int? {
    val byte = read() ?: return null
    if (byte < 0x80)
        return byte

    return (byte and 0x7F) or ((read() ?: return null) shl 7)
}

public suspend fun InputFlow.readFloatBE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }

public suspend fun InputFlow.readFloatLE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }

public suspend fun InputFlow.readFloat32BE(): Float? = this.readInt32BE()?.let { Float.fromBits(it) }

public suspend fun InputFlow.readFloat32LE(): Float? = this.readInt32LE()?.let { Float.fromBits(it) }

public suspend fun InputFlow.readDoubleBE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }

public suspend fun InputFlow.readDoubleLE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }

public suspend fun InputFlow.readFloat64BE(): Double? = this.readInt64BE()?.let { Double.fromBits(it) }

public suspend fun InputFlow.readFloat64LE(): Double? = this.readInt64LE()?.let { Double.fromBits(it) }