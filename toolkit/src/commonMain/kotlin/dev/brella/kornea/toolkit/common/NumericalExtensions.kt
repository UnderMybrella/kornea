package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince

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