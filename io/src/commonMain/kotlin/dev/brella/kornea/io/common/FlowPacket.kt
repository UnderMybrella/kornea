package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface FlowPacket {
    public val buffer: ByteArray
    public val size: Int get() = buffer.size
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline class Int16Packet(override val buffer: ByteArray = ByteArray(2)) : FlowPacket
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline class Int32Packet(override val buffer: ByteArray = ByteArray(4)) : FlowPacket
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline class Int64Packet(override val buffer: ByteArray = ByteArray(8)) : FlowPacket

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int16Packet.a: Byte
    get() = buffer[0]
    set(value) {
        buffer[0] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int16Packet.b: Byte
    get() = buffer[1]
    set(value) {
        buffer[1] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int32Packet.a: Byte
    get() = buffer[0]
    set(value) {
        buffer[0] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int32Packet.b: Byte
    get() = buffer[1]
    set(value) {
        buffer[1] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int32Packet.c: Byte
    get() = buffer[2]
    set(value) {
        buffer[2] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int32Packet.d: Byte
    get() = buffer[3]
    set(value) {
        buffer[3] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.a: Byte
    get() = buffer[0]
    set(value) {
        buffer[0] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.b: Byte
    get() = buffer[1]
    set(value) {
        buffer[1] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.c: Byte
    get() = buffer[2]
    set(value) {
        buffer[2] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.d: Byte
    get() = buffer[3]
    set(value) {
        buffer[3] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.e: Byte
    get() = buffer[4]
    set(value) {
        buffer[4] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.f: Byte
    get() = buffer[5]
    set(value) {
        buffer[5] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.g: Byte
    get() = buffer[6]
    set(value) {
        buffer[6] = value
    }

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline var Int64Packet.h: Byte
    get() = buffer[7]
    set(value) {
        buffer[7] = value
    }