package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.OutputFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.js.JsName

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public fun InputFlow.asFlow(bufferSize: Int = 8192, dataSize: Long = Long.MAX_VALUE): Flow<ByteArray> =
    flow {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        var bytesToCopy: Int
        while (bytes != null && bytesCopied < dataSize) {
            bytesToCopy = minOf(bytes, (dataSize - bytesCopied).toInt())
            emit(buffer.copyOfRange(0, bytesToCopy))
            bytesCopied += bytesToCopy
            bytes = read(buffer)
        }
    }

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public fun InputFlow.asFlowNoCopy(bufferSize: Int = 8192, dataSize: Long = Long.MAX_VALUE): Flow<Pair<ByteArray, Int>> =
    flow {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        var bytesToCopy: Int
        while (bytes != null && bytesCopied < dataSize) {
            bytesToCopy = minOf(bytes, (dataSize - bytesCopied).toInt())
            emit(Pair(buffer, bytesToCopy))
            bytesCopied += bytesToCopy
            bytes = read(buffer)
        }
    }

@ExperimentalUnsignedTypes
public suspend inline infix fun InputFlow.copyToOutputFlow(output: OutputFlow): Long = copyTo(output)
public suspend inline infix fun InputFlow.pipeTo(output: OutputFlow): Long = copyTo(output)

@ExperimentalUnsignedTypes
public suspend fun InputFlow.copyTo(output: OutputFlow, bufferSize: Int = 8192, dataSize: Int = Int.MAX_VALUE): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    var bytesToCopy: Int
    while (bytes != null && bytesCopied < dataSize) {
        bytesToCopy = minOf(bytes, (dataSize - bytesCopied).toInt())
        output.write(buffer, 0, bytesToCopy)
        bytesCopied += bytesToCopy
        bytes = read(buffer)
    }
    return bytesCopied
}