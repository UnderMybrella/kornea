package dev.brella.kornea.io.common

import dev.brella.kornea.io.common.flow.extensions.addInt16BE
import dev.brella.kornea.io.common.flow.extensions.addInt16LE

public enum class TextCharsets(public val bytesForNull: Int) {
    ASCII(1),
    UTF_8(1),
    UTF_16(2),
    UTF_16LE(2),
    UTF_16BE(2);

    public companion object {
        public const val UTF_16BE_BOM: Int = 0xFEFF
        public const val UTF_16LE_BOM: Int = 0xFFFE
        public const val UTF_16_BOM: Int = 0xFEFF
    }
}

public expect fun ByteArray.decodeToString(charset: TextCharsets): String
public inline fun ByteArray.decodeToUTF8String(): String = decodeToString(TextCharsets.UTF_8)
public inline fun ByteArray.decodeToUTF16String(): String = decodeToString(TextCharsets.UTF_16)
public inline fun ByteArray.decodeToUTF16LEString(): String = decodeToString(TextCharsets.UTF_16LE)
public inline fun ByteArray.decodeToUTF16BEString(): String = decodeToString(TextCharsets.UTF_16BE)

internal fun manuallyDecode(array: ByteArray, charset: TextCharsets): String {
    when (charset) {
        TextCharsets.ASCII -> return CharArray(array.size) { array[it].toInt().toChar() }.concatToString()
        TextCharsets.UTF_8 -> return array.decodeToString()
        TextCharsets.UTF_16 -> {
            if (array.size < 2)
                return ""

            val bom = toUTF16LE(array[0], array[1])

            val builder = StringBuilder()
            if (bom == TextCharsets.UTF_16BE_BOM) {
                for (i in 1 until array.size / 2)
                    builder.append(toUTF16BE(array[i * 2], array[i * 2 + 1]).toChar())
            } else {
                if (bom != TextCharsets.UTF_16LE_BOM)
                    builder.append(bom.toChar())
                for (i in 1 until array.size / 2)
                    builder.append(toUTF16LE(array[i * 2], array[i * 2 + 1]).toChar())
            }

            return builder.toString()
        }
        TextCharsets.UTF_16LE -> {
            val builder = StringBuilder()

            for (i in 0 until array.size / 2)
                builder.append(toUTF16LE(array[i * 2], array[i * 2 + 1]).toChar())

            return builder.toString()
        }
        TextCharsets.UTF_16BE -> {
            val builder = StringBuilder()

            for (i in 0 until array.size / 2)
                builder.append(toUTF16BE(array[i * 2], array[i * 2 + 1]).toChar())

            return builder.toString()
        }
    }
}

public expect fun String.encodeToByteArray(charset: TextCharsets): ByteArray
public inline fun String.encodeToUTF8ByteArray(): ByteArray = encodeToByteArray(TextCharsets.UTF_8)
public inline fun String.encodeToUTF16ByteArray(): ByteArray = encodeToByteArray(TextCharsets.UTF_16)
public inline fun String.encodeToUTF16LEByteArray(): ByteArray = encodeToByteArray(TextCharsets.UTF_16LE)
public inline fun String.encodeToUTF16BEByteArray(): ByteArray = encodeToByteArray(TextCharsets.UTF_16BE)

internal fun manuallyEncode(text: String, charset: TextCharsets, includeByteOrderMarker: Boolean = true): ByteArray {
    when (charset) {
        TextCharsets.ASCII -> return ByteArray(text.length) { text[it].code.toByte() }
        TextCharsets.UTF_8 -> return text.encodeToByteArray()
        TextCharsets.UTF_16 -> return manuallyEncode(text, TextCharsets.UTF_16LE, includeByteOrderMarker)
        TextCharsets.UTF_16LE -> {
            val output: MutableList<Byte> = ArrayList(text.length * 2)

            if (includeByteOrderMarker)
                output.addInt16LE(TextCharsets.UTF_16_BOM)

            text.forEach { character -> output.addInt16LE(character.code) }

            return output.toByteArray()
        }
        TextCharsets.UTF_16BE -> {
            val output: MutableList<Byte> = ArrayList(text.length * 2)

            if (includeByteOrderMarker)
                output.addInt16BE(TextCharsets.UTF_16_BOM)

            text.forEach { character -> output.addInt16BE(character.code) }

            return output.toByteArray()
        }
    }
}