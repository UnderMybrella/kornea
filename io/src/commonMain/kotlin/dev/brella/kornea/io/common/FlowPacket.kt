package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.jvm.JvmInline

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface FlowPacket {
    public val buffer: ByteArray
    public val size: Int get() = buffer.size
}

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public inline operator fun FlowPacket.get(index: Int): Byte = buffer[index]

@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public inline operator fun FlowPacket.set(index: Int, byte: Number) {
    buffer[index] = byte.toByte()
}

public interface IntFlowPacket : FlowPacket {
    public interface Int8 : IntFlowPacket {
        override val size: Int
            get() = 1
    }

    public interface Int16 : Int8 {
        override val size: Int
            get() = 2
    }

    public interface Int24 : Int16 {
        override val size: Int
            get() = 3
    }

    public interface Int32 : Int24 {
        override val size: Int
            get() = 4
    }

    public interface Int40 : Int32 {
        override val size: Int
            get() = 5
    }

    public interface Int48 : Int40 {
        override val size: Int
            get() = 6
    }

    public interface Int56 : Int48 {
        override val size: Int
            get() = 7
    }

    public interface Int64 : Int56 {
        override val size: Int
            get() = 8
    }
}

@JvmInline
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public value class Int16Packet(override val buffer: ByteArray = ByteArray(2)) : FlowPacket, IntFlowPacket.Int16

@JvmInline
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public value class Int24Packet(override val buffer: ByteArray = ByteArray(3)) : FlowPacket, IntFlowPacket.Int24

@JvmInline
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public value class Int32Packet(override val buffer: ByteArray = ByteArray(4)) : FlowPacket, IntFlowPacket.Int32

@JvmInline
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public value class Int40Packet(override val buffer: ByteArray = ByteArray(5)) : FlowPacket, IntFlowPacket.Int40

@JvmInline
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public value class Int48Packet(override val buffer: ByteArray = ByteArray(6)) : FlowPacket, IntFlowPacket.Int48

@JvmInline
@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public value class Int56Packet(override val buffer: ByteArray = ByteArray(7)) : FlowPacket, IntFlowPacket.Int56

@JvmInline
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public value class Int64Packet(override val buffer: ByteArray = ByteArray(8)) : FlowPacket, IntFlowPacket.Int64

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int8.a: Byte
    get() = buffer[0]
    set(value) {
        buffer[0] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int16.b: Byte
    get() = buffer[1]
    set(value) {
        buffer[1] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int24.c: Byte
    get() = buffer[2]
    set(value) {
        buffer[2] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int32.d: Byte
    get() = buffer[3]
    set(value) {
        buffer[3] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int40.e: Byte
    get() = buffer[4]
    set(value) {
        buffer[4] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int48.f: Byte
    get() = buffer[5]
    set(value) {
        buffer[5] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int56.g: Byte
    get() = buffer[6]
    set(value) {
        buffer[6] = value
    }

@AvailableSince(KorneaIO.VERSION_3_3_0_ALPHA)
public inline var IntFlowPacket.Int64.h: Byte
    get() = buffer[7]
    set(value) {
        buffer[7] = value
    }