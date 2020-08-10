package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.*
import dev.brella.kornea.io.common.set
import dev.brella.kornea.toolkit.common.asByte
import dev.brella.kornea.toolkit.common.asInt
import kotlin.experimental.or

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt64LE(num: Number) where T: Int64FlowState, T: OutputFlowState<*> {
    int64Packet.writeInt64LE(num)
    writePacket(int64Packet)
}
@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt64BE(num: Number) where T: Int64FlowState, T: OutputFlowState<*> {
    int64Packet.writeInt64BE(num)
    writePacket(int64Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt56LE(num: Number) where T: Int56FlowState, T: OutputFlowState<*> {
    int56Packet.writeInt56LE(num)
    writePacket(int56Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt56BE(num: Number) where T: Int56FlowState, T: OutputFlowState<*> {
    int56Packet.writeInt56BE(num)
    writePacket(int56Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt48LE(num: Number) where T: Int48FlowState, T: OutputFlowState<*> {
    int48Packet.writeInt48LE(num)
    writePacket(int48Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt48BE(num: Number) where T: Int48FlowState, T: OutputFlowState<*> {
    int48Packet.writeInt48BE(num)
    writePacket(int48Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt40LE(num: Number) where T: Int40FlowState, T: OutputFlowState<*> {
    int40Packet.writeInt40LE(num)
    writePacket(int40Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt40BE(num: Number) where T: Int40FlowState, T: OutputFlowState<*> {
    int40Packet.writeInt40BE(num)
    writePacket(int40Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt32LE(num: Number) where T: Int32FlowState, T: OutputFlowState<*> {
    int32Packet.writeInt32LE(num)
    writePacket(int32Packet)
}
@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt32BE(num: Number) where T: Int32FlowState, T: OutputFlowState<*> {
    int32Packet.writeInt32BE(num)
    writePacket(int32Packet)
}

@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: ULong): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32LE(num.toLong())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: UInt): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32LE(num.toInt())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: UShort): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32LE(num.toShort())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: UByte): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32LE(num.toByte())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32LE(num)

@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: ULong): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32BE(num.toLong())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: UInt): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32BE(num.toInt())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: UShort): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32BE(num.toShort())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: UByte): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32BE(num.toByte())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = writeInt32BE(num)

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt24LE(num: Number) where T: Int24FlowState, T: OutputFlowState<*> {
    int24Packet.writeInt24LE(num)
    writePacket(int24Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt24BE(num: Number) where T: Int24FlowState, T: OutputFlowState<*> {
    int24Packet.writeInt24BE(num)
    writePacket(int24Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt16LE(num: Number) where T: Int16FlowState, T: OutputFlowState<*> {
    int16Packet.writeInt16LE(num)
    writePacket(int16Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt16BE(num: Number) where T: Int16FlowState, T: OutputFlowState<*> {
    int16Packet.writeInt16BE(num)
    writePacket(int16Packet)
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public suspend fun <T> T.writeVariableInt16(num: Number) where T: Int16FlowState, T: OutputFlowState<*> {
    val short = num.toShort()

    if (short < 0x80) {
        write(short.asInt(0, 0xFF))
    } else {
        int16Packet[0] = short or 0x80
        int16Packet[1] = short.asByte(7)

        writePacket(int16Packet)
    }
}
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloatBE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt32BE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloatLE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt32LE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat32BE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt32BE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat32LE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt32LE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeDoubleBE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt64BE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeDoubleLE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt64LE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat64BE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt64BE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat64LE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState<*> = this.writeInt64LE(num.toDouble().toBits())