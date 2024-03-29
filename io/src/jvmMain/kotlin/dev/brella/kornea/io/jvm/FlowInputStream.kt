package dev.brella.kornea.io.jvm

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.toolkit.common.oneTimeMutableInline
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.ByteBuffer

@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public class FlowInputStream private constructor(
    private val flow: InputFlow,
    private val closeFlow: Boolean,
    private val bufferSize: Int = 8192,
    channelLimit: Int = 4
) : InputStream() {
    public companion object {
        @ExperimentalCoroutinesApi
        public operator fun invoke(
            scope: CoroutineScope,
            flow: InputFlow,
            closeFlow: Boolean,
            bufferSize: Int = 8192,
            channelLimit: Int = 4
        ): FlowInputStream {
            val stream =
                FlowInputStream(flow, closeFlow, bufferSize, channelLimit)
            stream.init(scope)
            return stream
        }
    }

    private val sendBufferToFlow = Channel<ByteBuffer>(channelLimit)
    private val receiveBufferFromFlow = Channel<ByteBuffer>(channelLimit)
    private var job: Job by oneTimeMutableInline()

    private var buffer: ByteBuffer? = null
    private fun fill() {
        buffer?.let { sendBufferToFlow.trySendBlocking(it) }
        buffer = receiveBufferFromFlow.tryReceive().getOrNull() ?: runBlocking { receiveBufferFromFlow.receive() }
    }

    override fun read(): Int {
        if (buffer?.hasRemaining() != true) {
            fill()
            if (buffer?.hasRemaining() != true) {
                return -1
            }
        }

        return buffer!!.get().toInt() and 0xFF
    }

    private fun read1(b: ByteArray, off: Int, len: Int): Int? {
        if (buffer?.hasRemaining() != true) {
            fill()

            if (buffer?.hasRemaining() != true) return null
        }

        val cnt = minOf(buffer!!.remaining(), len)
        buffer!!.get(b, off, cnt)
        return cnt
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if ((off or len or (off + len) or (b.size - (off + len))) < 0) {
            throw IndexOutOfBoundsException()
        } else if (len == 0) {
            return 0
        }

        var n = 0

        while (true) {
            val nread = read1(b, off + n, len - n) ?: 0
            if (nread <= 0)
                return if (n == 0) nread else n
            n += nread
            if (n >= len)
                return n
        }
    }

    override fun skip(n: Long): Long {
        if (buffer?.hasRemaining() != true) return 0
        val avail = minOf(n.toInt(), buffer!!.remaining())
        if (avail <= 0) return 0

        buffer!!.positionSafe(buffer!!.position() + avail)
        return avail.toLong()
    }

    override fun close() {
        super.close()

        sendBufferToFlow.close()
        receiveBufferFromFlow.close()
        job.cancel()

        if (closeFlow) {
            runBlocking { flow.close() }
        }
    }

    public suspend fun join(): Unit = job.join()

    @ExperimentalCoroutinesApi
    private fun init(scope: CoroutineScope) {
        job = scope.launch {
            val byteArray = ByteArray(bufferSize)
            while (isActive && !flow.isClosed && !receiveBufferFromFlow.isClosedForSend && !sendBufferToFlow.isClosedForReceive) {
                val buffer = sendBufferToFlow.receiveCatching().getOrNull() ?: break
                buffer.clearSafe()

                var read: Int
                while (buffer.hasRemaining() && flow.remaining() != null) {
                    read = flow.read(byteArray, 0, minOf(byteArray.size, buffer.remaining())) ?: break
                    buffer.put(byteArray, 0, read)
                }

                if (receiveBufferFromFlow.isClosedForSend) break
                if (buffer.position() == 0) {
                    receiveBufferFromFlow.close()
                    break
                } else if (flow.remaining() == null) {
                    buffer.flipSafe()

                    receiveBufferFromFlow.send(buffer)
                    receiveBufferFromFlow.close()
                    break
                } else {
                    buffer.flipSafe()

                    receiveBufferFromFlow.send(buffer)

                    delay(50)
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Suppress("FunctionName")
public fun CoroutineScope.FlowInputStream(
    flow: InputFlow,
    closeFlow: Boolean,
    bufferSize: Int = 8192,
    channelLimit: Int = 4
): FlowInputStream =
    FlowInputStream(this, flow, closeFlow, bufferSize, channelLimit)

@ExperimentalCoroutinesApi
public suspend inline fun <T> CoroutineScope.asInputStream(
    flow: InputFlow,
    closeFlow: Boolean,
    bufferSize: Int = 8192,
    channelLimit: Int = 4,
    block: (InputStream) -> T
): T {
    val stream =
        FlowInputStream(this, flow, closeFlow, bufferSize, channelLimit)
    val input = BufferedInputStream(stream).use(block)
    stream.join()
    return input
}