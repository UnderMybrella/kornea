package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.*

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekInt64LE(): Long? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int64Packet)?.readInt64LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekInt64BE(): Long? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int64Packet)?.readInt64BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekUInt64LE(): ULong? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int64Packet)?.readUInt64LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekUInt64BE(): ULong? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int64Packet)?.readUInt64BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekInt32LE(): Int? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int32Packet)?.readInt32LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekInt32BE(): Int? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int32Packet)?.readInt32BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekUInt32LE(): UInt? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int32Packet)?.readUInt32LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekUInt32BE(): UInt? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int32Packet)?.readUInt32BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekInt16LE(): Int? where T: Int16FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int16Packet)?.readInt16LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekInt16BE(): Int? where T: Int16FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int16Packet)?.readInt16BE()

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public suspend fun <T> T.peekVariableInt16(): Int? where T: Int16FlowState, T: InputFlowState<PeekableInputFlow> =
    peekPacket(int16Packet)?.readVariableInt16()

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekFloatBE(): Float? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> = peekInt32BE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekFloatLE(): Float? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> = peekInt32LE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekFloat32BE(): Float? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> = peekInt32BE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekFloat32LE(): Float? where T: Int32FlowState, T: InputFlowState<PeekableInputFlow> = peekInt32LE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekDoubleBE(): Double? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> = peekInt64BE()?.let { Double.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekDoubleLE(): Double? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> = peekInt64LE()?.let { Double.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekFloat64BE(): Double? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> = peekInt64BE()?.let { Double.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.peekFloat64LE(): Double? where T: Int64FlowState, T: InputFlowState<PeekableInputFlow> = peekInt64LE()?.let { Double.fromBits(it) }


/** Reading */


@ExperimentalUnsignedTypes
public suspend fun <T> T.readInt64LE(): Long? where T: Int64FlowState, T: InputFlow =
    readPacket(int64Packet)?.readInt64LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readInt64BE(): Long? where T: Int64FlowState, T: InputFlow =
    readPacket(int64Packet)?.readInt64BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readUInt64LE(): ULong? where T: Int64FlowState, T: InputFlow =
    readPacket(int64Packet)?.readUInt64LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readUInt64BE(): ULong? where T: Int64FlowState, T: InputFlow =
    readPacket(int64Packet)?.readUInt64BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readInt32LE(): Int? where T: Int32FlowState, T: InputFlow =
    readPacket(int32Packet)?.readInt32LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readInt32BE(): Int? where T: Int32FlowState, T: InputFlow =
    readPacket(int32Packet)?.readInt32BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readUInt32LE(): UInt? where T: Int32FlowState, T: InputFlow =
    readPacket(int32Packet)?.readUInt32LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readUInt32BE(): UInt? where T: Int32FlowState, T: InputFlow =
    readPacket(int32Packet)?.readUInt32BE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readInt16LE(): Int? where T: Int16FlowState, T: InputFlow =
    readPacket(int16Packet)?.readInt16LE()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readInt16BE(): Int? where T: Int16FlowState, T: InputFlow =
    readPacket(int16Packet)?.readInt16BE()

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public suspend fun <T> T.readVariableInt16(): Int? where T: Int16FlowState, T: InputFlow =
    readPacket(int16Packet)?.readVariableInt16()

@ExperimentalUnsignedTypes
public suspend fun <T> T.readFloatBE(): Float? where T: Int16FlowState, T: InputFlow = readInt32BE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readFloatLE(): Float? where T: Int16FlowState, T: InputFlow = readInt32LE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readFloat32BE(): Float? where T: Int16FlowState, T: InputFlow = readInt32BE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readFloat32LE(): Float? where T: Int16FlowState, T: InputFlow = readInt32LE()?.let { Float.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readDoubleBE(): Double? where T: Int16FlowState, T: InputFlow = readInt64BE()?.let { Double.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readDoubleLE(): Double? where T: Int16FlowState, T: InputFlow = readInt64LE()?.let { Double.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readFloat64BE(): Double? where T: Int16FlowState, T: InputFlow = readInt64BE()?.let { Double.fromBits(it) }

@ExperimentalUnsignedTypes
public suspend fun <T> T.readFloat64LE(): Double? where T: Int16FlowState, T: InputFlow = readInt64LE()?.let { Double.fromBits(it) }
