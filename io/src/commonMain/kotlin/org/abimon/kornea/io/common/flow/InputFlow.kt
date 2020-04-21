@file:Suppress("NOTHING_TO_INLINE")

package org.abimon.kornea.io.common.flow

import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.erorrs.common.map
import org.abimon.kornea.io.common.*

@ExperimentalUnsignedTypes
interface InputFlow : ObservableDataCloseable {
    companion object {
        const val FROM_BEGINNING = 0
        const val FROM_END = 1
        const val FROM_POSITION = 2
    }

    val location: String?

    suspend fun read(): Int?
    suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    suspend fun read(b: ByteArray, off: Int, len: Int): Int?
    suspend fun skip(n: ULong): ULong?
    suspend fun seek(pos: Long, mode: Int): ULong? = null
    suspend fun position(): ULong

    suspend fun available(): ULong?
    suspend fun remaining(): ULong?
    suspend fun size(): ULong?
}

@ExperimentalUnsignedTypes
interface PeekableInputFlow: InputFlow {
    suspend fun peek(forward: Int = 1): Int?
}

@ExperimentalUnsignedTypes
suspend inline fun InputFlow.skip(number: Number): ULong? = skip(number.toLong().toULong())

@ExperimentalUnsignedTypes
suspend fun InputFlow.readBytes(bufferSize: Int = 8192): ByteArray {
    val buffer = BinaryOutputFlow()
    copyTo(buffer, bufferSize)
    return buffer.getData()
}

@ExperimentalUnsignedTypes
suspend fun InputFlow.readExact(buffer: ByteArray): ByteArray? = readExact(buffer, 0, buffer.size)

@ExperimentalUnsignedTypes
suspend fun InputFlow.readExact(buffer: ByteArray, offset: Int, length: Int): ByteArray? {
    var currentOffset: Int = offset
    var remainingLength: Int = length

    var read: Int

    while (remainingLength > 0 && (currentOffset + remainingLength) <= buffer.size) {
        read = read(buffer, currentOffset, remainingLength) ?: break
        currentOffset += read
        remainingLength -= read
    }

    return if (remainingLength == 0) buffer else null
}

@ExperimentalUnsignedTypes
suspend fun InputFlow.readAndClose(bufferSize: Int = 8192): ByteArray {
    use(this) {
        val buffer = BinaryOutputFlow()
        copyTo(buffer, bufferSize)
        return buffer.getData()
    }
}

@ExperimentalUnsignedTypes
suspend inline fun <F: InputFlow, reified T> F.fauxSeekFromStart(offset: ULong, dataSource: DataSource<out F>, block: (F) -> T): KorneaResult<T> {
    val bookmark = position()
    return if (seek(offset.toLong(), InputFlow.FROM_BEGINNING) == null) {
        dataSource.openInputFlow().map { flow ->
            use(flow) {
                flow.skip(offset)
                block(flow)
            }
        }
    } else {
        val result = block(this)
        seek(bookmark.toLong(), InputFlow.FROM_BEGINNING)
        KorneaResult.Success(result)
    }
}

fun readResultIsValid(byte: Int): Boolean = byte != -1

@ExperimentalUnsignedTypes
public suspend inline fun <T : InputFlow, R> bookmark(t: T, block: () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), InputFlow.FROM_BEGINNING)
    }
}

@ExperimentalUnsignedTypes
suspend fun InputFlow.globalOffset(): ULong = if (this is SinkOffsetInputFlow) baseOffset + backing.globalOffset() else if (this is WindowedInputFlow) baseOffset + window.globalOffset() else 0u
@ExperimentalUnsignedTypes
suspend fun InputFlow.offsetPosition(): ULong = globalOffset() + position()