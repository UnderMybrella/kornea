package dev.brella.kornea.io.common

@Suppress("NOTHING_TO_INLINE")
public inline fun toUTF16LE(higherByte: Byte, lowerByte: Byte): Int = ((higherByte.toInt() and 0xFF) shl 8) or (lowerByte.toInt() and 0xFF)
@Suppress("NOTHING_TO_INLINE")
public inline fun toUTF16BE(lowerByte: Byte, higherByte: Byte): Int = ((higherByte.toInt() and 0xFF) shl 8) or (lowerByte.toInt() and 0xFF)

public inline infix fun Int.isBitSet(mask: Int): Boolean = this and mask == mask