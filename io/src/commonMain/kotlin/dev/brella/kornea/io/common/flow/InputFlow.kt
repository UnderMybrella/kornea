@file:Suppress("NOTHING_TO_INLINE")

package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.closeAfter
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.errors.common.switchIfEmpty
import dev.brella.kornea.io.common.*
import dev.brella.kornea.io.common.flow.extensions.copyTo

public interface InputFlow : KorneaFlow {
    public companion object {
    }

    public override val location: String?

    public override suspend fun position(): ULong
    public override fun locationAsUri(): KorneaResult<Uri>

    public suspend fun read(): Int?
    public suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    public suspend fun read(b: ByteArray, off: Int, len: Int): Int?
    public suspend fun skip(n: ULong): ULong?

    public suspend fun available(): ULong?
    public suspend fun remaining(): ULong?
    public suspend fun size(): ULong?
}

public interface InputFlowConstituent: KorneaFlowConstituent {
    override val flow: InputFlow
}

public suspend inline fun InputFlow.skip(number: Number): ULong? = skip(number.toLong().toULong())

public suspend fun InputFlow.readBytes(bufferSize: Int = 8192, dataSize: Int = Int.MAX_VALUE): ByteArray {
    val buffer = BinaryOutputFlow()
    copyTo(buffer, bufferSize, dataSize)
    return buffer.getData()
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun InputFlow.readPacket(packet: FlowPacket): ByteArray? = if (read(packet.buffer, 0, packet.size) == packet.size) packet.buffer else null

public suspend fun InputFlow.readExact(count: Int): ByteArray? = readExact(ByteArray(count), 0, count)

public suspend fun InputFlow.readExact(buffer: ByteArray): ByteArray? = readExact(buffer, 0, buffer.size)

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

public suspend fun InputFlow.readAndClose(bufferSize: Int = 8192): ByteArray =
    closeAfter(this) {
        val buffer = BinaryOutputFlow()
        copyTo(buffer, bufferSize)

        buffer.getData()
    }

public suspend inline fun <reified F: InputFlow, reified T> F.fauxSeekFromStart(offset: ULong, dataSource: DataSource<F>, crossinline block: suspend (F) -> T): KorneaResult<T> =
    seekable {
        bookmark(offset.toLong(), EnumSeekMode.FROM_BEGINNING) {
            block(this@fauxSeekFromStart)
        }
    }.switchIfEmpty {
        dataSource.openInputFlow().map { flow ->
            closeAfter(flow) {
                flow.skip(offset)
                block(flow)
            }
        }
    }

public inline fun readResultIsValid(byte: Int): Boolean = byte != -1

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