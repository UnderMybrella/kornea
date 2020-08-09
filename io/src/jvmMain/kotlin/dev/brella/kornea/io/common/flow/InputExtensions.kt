package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.jvm.flipSafe
import dev.brella.kornea.io.jvm.limitSafe
import dev.brella.kornea.io.jvm.positionSafe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public fun InputFlow.asFlowNIO(bufferSize: Int = 8192, dataSize: Long = Long.MAX_VALUE): Flow<ByteBuffer> =
    flow {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        val nio = ByteBuffer.wrap(buffer)
        var bytes = read(buffer)
        var bytesToCopy: Int
        while (bytes != null && bytesCopied < dataSize) {
            bytesToCopy = minOf(bytes, (dataSize - bytesCopied).toInt())
            nio.positionSafe(0)
                .limitSafe(bytesToCopy)

            emit(nio)
            bytesCopied += bytesToCopy
            bytes = read(buffer)
        }
    }