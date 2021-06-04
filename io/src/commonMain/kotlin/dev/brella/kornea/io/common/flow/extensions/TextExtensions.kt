package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.io.common.TextCharsets
import dev.brella.kornea.io.common.decodeToString
import dev.brella.kornea.io.common.flow.BinaryOutputFlow
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.readExact

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readString(len: Int, encoding: TextCharsets, overrideMaxLen: Boolean = false): String? {
    val data = readExact(ByteArray(if (overrideMaxLen) len.coerceAtLeast(0) else len.coerceIn(0, 1024 * 1024)))
    return data?.decodeToString(encoding)
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readAsciiString(len: Int, overrideMaxLen: Boolean = false): String? {
    val data = ByteArray(if (overrideMaxLen) len.coerceAtLeast(0) else len.coerceIn(0, 1024 * 1024))
    if (read(data) != data.size) return null
    return data.decodeToString()
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readNullTerminatedUTF8String(): String = readNullTerminatedString(encoding = TextCharsets.UTF_8)

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readNullTerminatedString(maxLen: Int = 255, encoding: TextCharsets = TextCharsets.UTF_8): String {
    val data = BinaryOutputFlow()

    when (encoding.bytesForNull) {
        1 -> while (true) {
            val read = read() ?: break //This **should** work
            require(read != -1) { "Uho..., it's -1 somehow" }
            if (read == 0x00)
                break

            data.write(read)
        }
        2 -> while (true) {
            val read = readInt16LE() ?: break //This **should** work
            require(read != -1) { "Uho..., it's -1 somehow" }
            if (read == 0x00)
                break

            data.writeInt16LE(read)
        }
        4 -> while (true) {
            val read = readInt32LE() ?: break //This **should** work
            require(read != -1) { "Uho..., it's -1 somehow" }
            if (read == 0x00)
                break

            data.writeInt32LE(read)
        }
        8 -> while (true) {
            val read = readInt64LE() ?: break //This **should** work
            require(read != -1L) { "Uho..., it's -1 somehow" }
            if (read == 0x00L)
                break

            data.writeInt64LE(read)
        }
        else -> throw IllegalArgumentException("Invalid charset (Number of bytes is ${encoding.bytesForNull})")
    }

    return data.getData().decodeToString(encoding)
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readSingleByteNullTerminatedString(maxLen: Int = 255, encoding: TextCharsets = TextCharsets.UTF_8): String {
    val data = BinaryOutputFlow()

    while (true) {
        val read = read() ?: break
        require(read != -1) { "Uho..., it's -1 somehow" }
        if (read == 0x00)
            break

        data.write(read)
    }

    return data.getData().decodeToString(encoding)
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readDoubleByteNullTerminatedString(maxLen: Int = 255, encoding: TextCharsets = TextCharsets.UTF_16): String {
    val data = BinaryOutputFlow()

    while (true) {
        val read = readInt16LE() ?: break
        require(read != -1) { "Uho..., it's -1 somehow" }
        if (read == 0x00)
            break

        data.writeInt16LE(read)
    }

    return data.getData().decodeToString(encoding)
}