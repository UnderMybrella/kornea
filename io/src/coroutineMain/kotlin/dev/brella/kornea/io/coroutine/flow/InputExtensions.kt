package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.InputFlow
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