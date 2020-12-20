package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.math.log
import kotlin.math.max

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asShort(): Short = toInt().and(0xFF).toShort()

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asUShort(): UShort = toUShort() and 0xFFu

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asInt(): Int = toInt() and 0xFF

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asUInt(): UInt = toUInt() and 0xFFu

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asLong(): Long = toLong() and 0xFF

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asULong(): ULong = toULong() and 0xFFu
public inline fun Byte.asDouble(): Double = toInt().and(0xFF).toDouble()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asShort(leftBits: Int): Short = toInt().and(0xFF).shl(leftBits).toShort()

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asUShort(leftBits: Int): UShort = toInt().and(0xFF).shl(leftBits).toUShort()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asInt(leftBits: Int): Int = toInt().and(0xFF).shl(leftBits)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asUInt(leftBits: Int): UInt = toUInt().and(0xFFu).shl(leftBits)

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asLong(leftBits: Int): Long = toLong().and(0xFF).shl(leftBits)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Byte.asULong(leftBits: Int): ULong = toULong().and(0xFFu).shl(leftBits)

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Short.asByte(): Byte = toByte()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Short.asByte(rightBits: Int): Byte = toInt().shr(rightBits).toByte()

public inline fun Short.asInt(): Int = toInt().and(0xFFFF)

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Short.asInt(rightBits: Int, mask: Int): Int = toInt().shr(rightBits).and(mask)

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Int.asByte(): Byte = toByte()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Int.asByte(rightBits: Int): Byte = shr(rightBits).toByte()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Int.asInt(rightBits: Int, mask: Int): Int = shr(rightBits).and(mask)

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Long.asByte(): Byte = toByte()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Long.asByte(rightBits: Int): Byte = shr(rightBits).toByte()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Long.asInt(rightBits: Int): Int = shr(rightBits).toInt()

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun Long.asInt(rightBits: Int, mask: Long): Int = shr(rightBits).and(mask).toInt()

private val HEX_ARRAY_UPPER = "0123456789ABCDEF".toCharArray()
private val HEX_ARRAY_LOWER = "0123456789abcdef".toCharArray()

private const val HEX_LONG = 0xF.toLong()
private const val ZERO_BYTE = 0.toByte()
private const val ZERO_SHORT = 0.toShort()
private const val ZERO_PREFIXED_STRING = "0x0"
private const val ZERO_STRING = "0"

private val HEX_INDEX_ARRAYS by lazy {
    Array(17) { i ->
        val max = (i-1) shl 2
        IntArray(i) { max - (it shl 2) }
    }
}

public fun Number.toHexString(prefixed: Boolean = true, upper: Boolean = true): String {
    val long = when (this) {
        is Byte -> toLong().and(0xFF)
        is Short -> toLong().and(0xFFFF)
        is Int -> toLong().and(0xFFFFFFFFL)
        else -> toLong().and(0xFFFFFFFFFFFFFFF)
    }

    val hex = if (upper) HEX_ARRAY_UPPER else HEX_ARRAY_LOWER

    if (prefixed) {
        if (this == ZERO_BYTE) return ZERO_PREFIXED_STRING

        val baseLen = log(long.toDouble(), 16.0).toInt() + 1

        val array = CharArray(baseLen + 2)
        array[0] = '0'
        array[1] = 'x'
        val indices = HEX_INDEX_ARRAYS[baseLen]
        var i = 2
        indices.indices.forEach { j ->
            array[i++] = hex[((long shr indices[j]) and HEX_LONG).toInt()]
        }
        return array.concatToString()
    } else {
        if (this == ZERO_BYTE) return ZERO_STRING

        val len = log(long.toDouble(), 16.0).toInt() + 1
        val indices = HEX_INDEX_ARRAYS[len]
        return CharArray(len) { i -> hex[((long shr indices[i]) and HEX_LONG).toInt()] }.concatToString()
    }
}

public fun Byte.toHexString(paddedLength: Int?, prefixed: Boolean = true, upper: Boolean = true): String {
    val int = asInt()

    val hex = if (upper) HEX_ARRAY_UPPER else HEX_ARRAY_LOWER
    val paddedLength = paddedLength ?: 2

    if (prefixed) {
        if (this == ZERO_BYTE) return CharArray(paddedLength + 2) { '0' }.apply { this[1] = 'x' }.concatToString()

        val baseLen = log(int.toDouble(), 16.0).toInt() + 1

        val array = CharArray((if (baseLen > paddedLength) baseLen else paddedLength) + 2)
        array[0] = '0'
        array[1] = 'x'
        val indices = HEX_INDEX_ARRAYS[baseLen]

        var i = 2
        var j = 0

        while (i < 2 + (paddedLength - baseLen)) {
            array[i++] = '0'
        }

        do {
            array[i++] = hex[(int shr indices[j++]) and 0xF]
        } while (i in array.indices)

        return array.concatToString()
    } else {
        if (this == ZERO_BYTE) return CharArray(paddedLength) { '0' }.concatToString()

        val len = log(int.toDouble(), 16.0).toInt() + 1
        val indices = HEX_INDEX_ARRAYS[len]
//        return CharArray(len) { i -> hex[(int shr indices[i]) and 0xF] }.concatToString()

        val array = CharArray((if (len > paddedLength) len else paddedLength) + 2)

        var i = 0
        var j = 0

        while (i < paddedLength - len) {
            array[i++] = '0'
        }

        do {
            array[i++] = hex[(int shr indices[j++]) and 0xF]
        } while (i in array.indices)

        return array.concatToString()
    }
}

public fun Number.toHexString(paddedLength: Int?, prefixed: Boolean = true, upper: Boolean = true): String {
    val long = when (this) {
        is Byte -> toLong().and(0xFF)
        is Short -> toLong().and(0xFFFF)
        is Int -> toLong().and(0xFFFFFFFFL)
        else -> toLong().and(0xFFFFFFFFFFFFFFF)
    }

    val hex = if (upper) HEX_ARRAY_UPPER else HEX_ARRAY_LOWER
    if (prefixed) {
        if (this == ZERO_SHORT) return CharArray(paddedLength?.plus(2) ?: 6) { '0' }.apply { this[1] = 'x' }
            .concatToString()

        val log = log(long.toDouble(), 16.0).toInt()
        val len =
            if (paddedLength == null) ((log shr 1) + 1) shl 1
            else max(log + 1, paddedLength.coerceAtMost(16))

        val array = CharArray(len + 2) { '0' }
        array[1] = 'x'
        val indices = HEX_INDEX_ARRAYS[len]

        var i = 2
        var j = 0

        do {
            array[i++] = hex[((long shr indices[j++]) and HEX_LONG).toInt()]
        } while (i in array.indices)

        return array.concatToString()
    } else {
        if (this == ZERO_SHORT) return CharArray(paddedLength?.plus(2) ?: 4) { '0' }.concatToString()

        val log = log(long.toDouble(), 16.0).toInt()
        val len =
            if (paddedLength == null) ((log shr 1) + 1) shl 1
            else max(log + 1, paddedLength.coerceAtMost(16))

        val indices = HEX_INDEX_ARRAYS[len]

        return CharArray(len) { hex[((long shr indices[it]) and HEX_LONG).toInt()] }.concatToString()
    }
}