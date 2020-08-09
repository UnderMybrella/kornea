@file:Suppress("unused")

package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.OutputFlow

public fun makeMask(vararg bits: Int): Int {
    var mask = 0
    for (bit in bits)
        mask = mask or ((1 shl (bit + 1)) - 1)

    return mask
}

public fun Number.toInt16LE(): IntArray {
    val num = toInt() and 0xFFFF
    return intArrayOf(num shr 8, num and 0xFF)
}

//@ExperimentalUnsignedTypes
//@ExperimentalStdlibApi
//suspend fun InputFlow.readString(len: Int, encoding: TextCharsets, overrideMaxLen: Boolean = false): String {
//    val data = ByteArray(if (overrideMaxLen) len.coerceAtLeast(0) else len.coerceIn(0, 1024 * 1024))
//    read(data)
//    return data.decodeToString(encoding)
//}
//
//@ExperimentalUnsignedTypes
//suspend fun InputFlow.readAsciiString(len: Int, overrideMaxLen: Boolean = false): String? {
//    val data = ByteArray(if (overrideMaxLen) len.coerceAtLeast(0) else len.coerceIn(0, 1024 * 1024))
//    if (read(data) != data.size) return null
//    return String(CharArray(data.size) { data[it].toChar() })
//}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readNumBytes(num: Int): ByteArray {
    val data = ByteArray(num)
    read(data)
    return data
}

//@ExperimentalUnsignedTypes
//@ExperimentalStdlibApi
//suspend fun InputFlow.readNullTerminatedUTF8String(): String = readNullTerminatedString(encoding = TextCharsets.UTF_8)
//
//@ExperimentalUnsignedTypes
//@ExperimentalStdlibApi
//suspend fun InputFlow.readNullTerminatedString(maxLen: Int = 255, encoding: TextCharsets = TextCharsets.UTF_8): String {
//    val data = BinaryOutputFlow()
//
//    while (true) {
//        val read = readIntXLE(encoding.bytesForNull) ?: break //This **should** work
//        require(read != -1) { "Uho..., it's -1 somehow" }
//        if (read == 0x00)
//            break
//
//        data.writeIntXLE(read, encoding.bytesForNull)
//    }
//
//    return data.getData().decodeToString(encoding)
//}
//
//@ExperimentalUnsignedTypes
//@ExperimentalStdlibApi
//suspend fun InputFlow.readSingleByteNullTerminatedString(maxLen: Int = 255, encoding: TextCharsets = TextCharsets.UTF_8): String {
//    val data = BinaryOutputFlow()
//
//    while (true) {
//        val read = read() ?: break
//        require(read != -1) { "Uho..., it's -1 somehow" }
//        if (read == 0x00)
//            break
//
//        data.write(read)
//    }
//
//    return data.getData().decodeToString(encoding)
//}
//
//@ExperimentalUnsignedTypes
//@ExperimentalStdlibApi
//suspend fun InputFlow.readDoubleByteNullTerminatedString(maxLen: Int = 255, encoding: TextCharsets = TextCharsets.UTF_8): String {
//    val data = BinaryOutputFlow()
//
//    while (true) {
//        val read = readInt16LE() ?: break
//        require(read != -1) { "Uho..., it's -1 somehow" }
//        if (read == 0x00)
//            break
//
//        data.writeInt16LE(read)
//    }
//
//    return data.getData().decodeToString(encoding)
//}

@ExperimentalUnsignedTypes
public suspend fun OutputFlow.copyFrom(input: InputFlow): Long = input.copyTo(this)
