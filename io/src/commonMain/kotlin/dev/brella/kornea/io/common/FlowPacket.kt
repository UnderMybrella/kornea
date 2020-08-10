package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface FlowPacket {
    public val buffer: ByteArray
    public val size: Int get() = buffer.size
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public inline operator fun FlowPacket.get(index: Int): Byte = buffer[index]
@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public inline operator fun FlowPacket.set(index: Int, byte: Number){
    buffer[index] = byte.toByte()
}

public interface IntFlowPacket: FlowPacket {
    public interface BITS_8: IntFlowPacket {
        override val size: Int
            get() = 1
    }
    public interface BITS_16: BITS_8 {
        override val size: Int
            get() = 2
    }
    public interface BITS_24: BITS_16 {
        override val size: Int
            get() = 3
    }
    public interface BITS_32: BITS_24 {
        override val size: Int
            get() = 4
    }
    public interface BITS_40: BITS_32 {
        override val size: Int
            get() = 5
    }
    public interface BITS_48: BITS_40 {
        override val size: Int
            get() = 6
    }
    public interface BITS_56: BITS_48 {
        override val size: Int
            get() = 7
    }
    public interface BITS_64: BITS_56 {
        override val size: Int
            get() = 8
    }
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline class Int16Packet(override val buffer: ByteArray = ByteArray(2)) : FlowPacket, IntFlowPacket.BITS_16
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline class Int24Packet(override val buffer: ByteArray = ByteArray(3)): FlowPacket, IntFlowPacket.BITS_24
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline class Int32Packet(override val buffer: ByteArray = ByteArray(4)) : FlowPacket, IntFlowPacket.BITS_32
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline class Int40Packet(override val buffer: ByteArray = ByteArray(5)): FlowPacket, IntFlowPacket.BITS_40
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline class Int48Packet(override val buffer: ByteArray = ByteArray(6)): FlowPacket, IntFlowPacket.BITS_48
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline class Int56Packet(override val buffer: ByteArray = ByteArray(7)): FlowPacket, IntFlowPacket.BITS_56
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline class Int64Packet(override val buffer: ByteArray = ByteArray(8)) : FlowPacket, IntFlowPacket.BITS_64

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_8.a: Byte
    get() = buffer[0]
    set(value) {
        buffer[0] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_16.b: Byte
    get() = buffer[1]
    set(value) {
        buffer[1] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_24.c: Byte
    get() = buffer[2]
    set(value) {
        buffer[2] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_32.d: Byte
    get() = buffer[3]
    set(value) {
        buffer[3] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_40.e: Byte
    get() = buffer[4]
    set(value) {
        buffer[4] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_48.f: Byte
    get() = buffer[5]
    set(value) {
        buffer[5] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_56.g: Byte
    get() = buffer[6]
    set(value) {
        buffer[6] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.BITS_64.h: Byte
    get() = buffer[7]
    set(value) {
        buffer[7] = value
    }