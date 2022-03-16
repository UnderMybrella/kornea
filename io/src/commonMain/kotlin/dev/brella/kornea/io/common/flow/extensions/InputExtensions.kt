package dev.brella.kornea.io.common.flow.extensions

import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.OutputFlow

public suspend inline infix fun InputFlow.copyToOutputFlow(output: OutputFlow): Long = copyTo(output)
public suspend inline infix fun InputFlow.pipeTo(output: OutputFlow): Long = copyTo(output)

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