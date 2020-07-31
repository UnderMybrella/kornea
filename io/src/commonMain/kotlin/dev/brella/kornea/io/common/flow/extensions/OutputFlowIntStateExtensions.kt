package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.io.common.flow.*
import dev.brella.kornea.toolkit.common.asInt

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt64LE(num: Number) where T: Int64FlowState, T: OutputFlowState {
    int64Packet.writeInt64LE(num)
    writePacket(int64Packet)
}
@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt64BE(num: Number) where T: Int64FlowState, T: OutputFlowState {
    int64Packet.writeInt64BE(num)
    writePacket(int64Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt32LE(num: Number) where T: Int32FlowState, T: OutputFlowState {
    int32Packet.writeInt32LE(num)
    writePacket(int32Packet)
}
@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt32BE(num: Number) where T: Int32FlowState, T: OutputFlowState {
    int32Packet.writeInt32BE(num)
    writePacket(int32Packet)
}

@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: ULong): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32LE(num.toLong())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: UInt): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32LE(num.toInt())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: UShort): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32LE(num.toShort())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: UByte): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32LE(num.toByte())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32LE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32LE(num)

@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: ULong): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32BE(num.toLong())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: UInt): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32BE(num.toInt())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: UShort): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32BE(num.toShort())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: UByte): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32BE(num.toByte())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeUInt32BE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = writeInt32BE(num)

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt16LE(num: Number) where T: Int16FlowState, T: OutputFlowState {
    int16Packet.writeInt16LE(num)
    writePacket(int16Packet)
}

@ExperimentalUnsignedTypes
public suspend fun <T> T.writeInt16BE(num: Number) where T: Int16FlowState, T: OutputFlowState {
    int16Packet.writeInt16BE(num)
    writePacket(int16Packet)
}

@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloatBE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt32BE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloatLE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt32LE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat32BE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt32BE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat32LE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt32LE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeDoubleBE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt64BE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeDoubleLE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt64LE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat64BE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt64BE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun <T> T.writeFloat64LE(num: Number): Unit where T: Int32FlowState, T: OutputFlowState = this.writeInt64LE(num.toDouble().toBits())