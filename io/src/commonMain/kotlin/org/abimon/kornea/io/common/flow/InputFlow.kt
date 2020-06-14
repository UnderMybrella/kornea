@file:Suppress("NOTHING_TO_INLINE")

package org.abimon.kornea.io.common.flow

import org.abimon.kornea.annotations.WrongBytecodeGenerated
import org.abimon.kornea.errors.common.KorneaResult
import org.abimon.kornea.errors.common.map
import org.abimon.kornea.io.common.*

@ExperimentalUnsignedTypes
public interface InputFlow : ObservableDataCloseable {
    public companion object {
        @Deprecated(replaceWith = ReplaceWith("EnumSeekMode.FROM_BEGINNING", "org.abimon.kornea.io.common.EnumSeekMode"), message = "Replace with generic seek constant", level = DeprecationLevel.ERROR)
        public const val FROM_BEGINNING: Int = 0
        @Deprecated(replaceWith = ReplaceWith("EnumSeekMode.FROM_END", "org.abimon.kornea.io.common.EnumSeekMode"), message = "Replace with generic seek constant", level = DeprecationLevel.ERROR)
        public const val FROM_END: Int = 1
        @Deprecated(replaceWith = ReplaceWith("EnumSeekMode.FROM_POSITION", "org.abimon.kornea.io.common.EnumSeekMode"), message = "Replace with generic seek constant", level = DeprecationLevel.ERROR)
        public const val FROM_POSITION: Int = 2
    }

    public val location: String?

    public suspend fun read(): Int?
    public suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    public suspend fun read(b: ByteArray, off: Int, len: Int): Int?
    public suspend fun skip(n: ULong): ULong?
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use SeekableInputFlow", level = DeprecationLevel.ERROR)
    public suspend fun seek(pos: Long, mode: Int): ULong? = null
    public suspend fun position(): ULong

    public suspend fun available(): ULong?
    public suspend fun remaining(): ULong?
    public suspend fun size(): ULong?
}

@ExperimentalUnsignedTypes
public interface PeekableInputFlow: InputFlow {
    public suspend fun peek(forward: Int = 1): Int?
}

@ExperimentalUnsignedTypes
public interface InputFlowWithBacking: InputFlow {
    public suspend fun globalOffset(): ULong
}

@ExperimentalUnsignedTypes
public interface OffsetInputFlow : InputFlow, InputFlowWithBacking {
    public val baseOffset: ULong
}

@ExperimentalUnsignedTypes
public interface SeekableInputFlow: InputFlow {
    public suspend fun seek(pos: Long, mode: EnumSeekMode): ULong
}

@ExperimentalUnsignedTypes
public suspend inline fun InputFlow.skip(number: Number): ULong? = skip(number.toLong().toULong())

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readBytes(bufferSize: Int = 8192): ByteArray {
    val buffer = BinaryOutputFlow()
    copyTo(buffer, bufferSize)
    return buffer.getData()
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readExact(count: Int): ByteArray? = readExact(ByteArray(count), 0, count)

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readExact(buffer: ByteArray): ByteArray? = readExact(buffer, 0, buffer.size)

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readExact(buffer: ByteArray, offset: Int, length: Int): ByteArray? {
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
public suspend fun InputFlow.readAndClose(bufferSize: Int = 8192): ByteArray =
    closeAfter(this) {
        val buffer = BinaryOutputFlow()
        copyTo(buffer, bufferSize)

        buffer.getData()
    }

@ExperimentalUnsignedTypes
public suspend inline fun <reified F: InputFlow, reified T> F.fauxSeekFromStart(offset: ULong, dataSource: DataSource<out F>, crossinline block: suspend (F) -> T): KorneaResult<T> {
    return if (this !is SeekableInputFlow) {
//        val flow = dataSource.openInputFlow() ?: return null
        dataSource.openInputFlow().map { flow ->
            closeAfter(flow) {
                flow.skip(offset)
                block(flow)
            }
        }
    } else {
        bookmark(this as SeekableInputFlow) {
            seek(offset.toLong(), EnumSeekMode.FROM_BEGINNING)
            val result = block(this)
            KorneaResult.success(result)
        }
    }
}

public inline fun readResultIsValid(byte: Int): Boolean = byte != -1

//@ExperimentalUnsignedTypes
//public suspend inline fun <T : SeekableInputFlow, R> T.bookmark(block: () -> R): R = bookmark(this, block)
@ExperimentalUnsignedTypes
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("bookmarkCrossinline(t, block)", "org.abimon.kornea.io.common.flow.bookmarkCrossinline"))
public suspend inline fun <T : SeekableInputFlow, R> bookmark(t: T, block: () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

@ExperimentalUnsignedTypes
public suspend inline fun <T : SeekableInputFlow, R> bookmarkCrossinline(t: T, crossinline block: suspend () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readChunked(bufferSize: Int = BufferedInputFlow.DEFAULT_BUFFER_SIZE, operation: (buffer: ByteArray, offset: Int, length: Int) -> Unit): Long? {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer) ?: return null
    while (bytes >= 0) {
        operation(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer) ?: return bytesCopied
    }
    return bytesCopied
}

@ExperimentalUnsignedTypes
public suspend fun InputFlow.globalOffset(): ULong = if (this is InputFlowWithBacking) this.globalOffset() else 0u
@ExperimentalUnsignedTypes
public suspend fun InputFlow.offsetPosition(): ULong = globalOffset() + position()