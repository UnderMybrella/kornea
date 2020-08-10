package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.toolkit.common.asInt
import kotlin.experimental.or

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt64LE(num: Number) {
    val long = num.toLong()

    write(long.asInt(0, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(32, 0xFF))
    write(long.asInt(40, 0xFF))
    write(long.asInt(48, 0xFF))
    write(long.asInt(56, 0xFF))
}
@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt64BE(num: Number) {
    val long = num.toLong()

    write(long.asInt(56, 0xFF))
    write(long.asInt(48, 0xFF))
    write(long.asInt(40, 0xFF))
    write(long.asInt(32, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt56LE(num: Number) {
    val long = num.toLong()

    write(long.asInt(0, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(32, 0xFF))
    write(long.asInt(40, 0xFF))
    write(long.asInt(48, 0xFF))
}
@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt56BE(num: Number) {
    val long = num.toLong()

    write(long.asInt(48, 0xFF))
    write(long.asInt(40, 0xFF))
    write(long.asInt(32, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt48LE(num: Number) {
    val long = num.toLong()

    write(long.asInt(0, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(32, 0xFF))
    write(long.asInt(40, 0xFF))
}
@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt48BE(num: Number) {
    val long = num.toLong()

    write(long.asInt(40, 0xFF))
    write(long.asInt(32, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt40LE(num: Number) {
    val long = num.toLong()

    write(long.asInt(0, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(32, 0xFF))
}
@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt40BE(num: Number) {
    val long = num.toLong()

    write(long.asInt(32, 0xFF))
    write(long.asInt(24, 0xFF))
    write(long.asInt(16, 0xFF))
    write(long.asInt(8, 0xFF))
    write(long.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt32LE(num: Number) {
    val int = num.toInt()

    write(int.asInt(0, 0xFF))
    write(int.asInt(8, 0xFF))
    write(int.asInt(16, 0xFF))
    write(int.asInt(24, 0xFF))
}
@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt32BE(num: Number) {
    val int = num.toInt()

    write(int.asInt(24, 0xFF))
    write(int.asInt(16, 0xFF))
    write(int.asInt(8, 0xFF))
    write(int.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32LE(num: ULong): Unit = writeInt32LE(num.toLong())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32LE(num: UInt): Unit = writeInt32LE(num.toInt())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32LE(num: UShort): Unit = writeInt32LE(num.toShort())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32LE(num: UByte): Unit = writeInt32LE(num.toByte())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32LE(num: Number): Unit = writeInt32LE(num)

@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32BE(num: ULong): Unit = writeInt32BE(num.toLong())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32BE(num: UInt): Unit = writeInt32BE(num.toInt())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32BE(num: UShort): Unit = writeInt32BE(num.toShort())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32BE(num: UByte): Unit = writeInt32BE(num.toByte())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeUInt32BE(num: Number): Unit = writeInt32BE(num)

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt24LE(num: Number) {
    val int = num.toInt()

    write(int.asInt(0, 0xFF))
    write(int.asInt(8, 0xFF))
    write(int.asInt(16, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt24BE(num: Number) {
    val int = num.toInt()

    write(int.asInt(16, 0xFF))
    write(int.asInt(8, 0xFF))
    write(int.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt16LE(num: Number) {
    val short = num.toShort()

    write(short.asInt(0, 0xFF))
    write(short.asInt(8, 0xFF))
}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.writeInt16BE(num: Number) {
    val short = num.toShort()

    write(short.asInt(8, 0xFF))
    write(short.asInt(0, 0xFF))
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_2_2_ALPHA)
public suspend fun OutputFlow.writeVariableInt16(num: Number) {
    val short = num.toShort()

    if (short < 0x80) {
        write(short.asInt(0, 0xFF))
    } else {
        write(short.or(0x80).asInt(0, 0xFF))
        write(short.asInt(7, 0xFF))
    }
}

@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeFloatBE(num: Number): Unit = this.writeInt32BE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeFloatLE(num: Number): Unit = this.writeInt32LE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeFloat32BE(num: Number): Unit = this.writeInt32BE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeFloat32LE(num: Number): Unit = this.writeInt32LE(num.toFloat().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeDoubleBE(num: Number): Unit = this.writeInt64BE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeDoubleLE(num: Number): Unit = this.writeInt64LE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeFloat64BE(num: Number): Unit = this.writeInt64BE(num.toDouble().toBits())
@ExperimentalUnsignedTypes
public suspend inline fun OutputFlow.writeFloat64LE(num: Number): Unit = this.writeInt64LE(num.toDouble().toBits())