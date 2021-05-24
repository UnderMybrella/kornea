@file:Suppress("NOTHING_TO_INLINE")

package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.ObservableDataCloseable
import dev.brella.kornea.base.common.closeAfter
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.*
import dev.brella.kornea.io.common.flow.extensions.copyTo

@ExperimentalUnsignedTypes
public interface InputFlow : ObservableDataCloseable {
    public companion object {
        @Deprecated(replaceWith = ReplaceWith("EnumSeekMode.FROM_BEGINNING", "dev.brella.kornea.io.common.EnumSeekMode"), message = "Replace with generic seek constant", level = DeprecationLevel.ERROR)
        public const val FROM_BEGINNING: Int = 0
        @Deprecated(replaceWith = ReplaceWith("EnumSeekMode.FROM_END", "dev.brella.kornea.io.common.EnumSeekMode"), message = "Replace with generic seek constant", level = DeprecationLevel.ERROR)
        public const val FROM_END: Int = 1
        @Deprecated(replaceWith = ReplaceWith("EnumSeekMode.FROM_POSITION", "dev.brella.kornea.io.common.EnumSeekMode"), message = "Replace with generic seek constant", level = DeprecationLevel.ERROR)
        public const val FROM_POSITION: Int = 2
    }

    public val location: String?

    public suspend fun read(): Int?
    public suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    public suspend fun read(b: ByteArray, off: Int, len: Int): Int?
    public suspend fun skip(n: ULong): ULong?
    public suspend fun position(): ULong

    public suspend fun available(): ULong?
    public suspend fun remaining(): ULong?
    public suspend fun size(): ULong?

    public fun locationAsUri(): KorneaResult<Uri>
}

@ExperimentalUnsignedTypes
public suspend inline fun InputFlow.skip(number: Number): ULong? = skip(number.toLong().toULong())

@ExperimentalUnsignedTypes
public suspend fun InputFlow.readBytes(bufferSize: Int = 8192, dataSize: Int = Int.MAX_VALUE): ByteArray {
    val buffer = BinaryOutputFlow()
    copyTo(buffer, bufferSize, dataSize)
    return buffer.getData()
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun InputFlow.readPacket(packet: FlowPacket): ByteArray? = if (read(packet.buffer, 0, packet.size) == packet.size) packet.buffer else null

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