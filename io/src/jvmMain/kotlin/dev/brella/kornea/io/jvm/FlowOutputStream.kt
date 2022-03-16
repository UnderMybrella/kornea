package dev.brella.kornea.io.jvm

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.toolkit.common.oneTimeMutableInline
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.BufferedOutputStream
import java.io.OutputStream

@ExperimentalCoroutinesApi
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public class FlowOutputStream private constructor(
    private val flow: OutputFlow,
    private val closeFlow: Boolean,
    private val bufferSize: Int = 8192,
    channelLimit: Int = bufferSize
) : OutputStream() {
    public companion object {
        public operator fun invoke(
            scope: CoroutineScope,
            flow: OutputFlow,
            closeFlow: Boolean,
            bufferSize: Int = 8192,
            channelLimit: Int = bufferSize
        ): FlowOutputStream {
            val stream =
                FlowOutputStream(flow, closeFlow, bufferSize, channelLimit)
            stream.init(scope)
            return stream
        }
    }

    private val buffered = atomic(0)
    private val outputChannel = Channel<Any>(channelLimit)
    private val updateChannel = Channel<Unit>(Channel.CONFLATED)
    private var job: Job by oneTimeMutableInline()

    private inline fun waitForOutputToClear(sizeNeeded: Int, crossinline block: suspend () -> Unit) {
        runBlocking {
            if (sizeNeeded > bufferSize) {
                while (isActive && !updateChannel.isEmpty) {
                    updateChannel.receive()
                    yield()
                }

                block()
                buffered += sizeNeeded
            } else {
                while (isActive && buffered.value + sizeNeeded < bufferSize) {
                    updateChannel.receive()
                    yield()
                }

                block()
                buffered += sizeNeeded
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun waitAndSend(byte: Int) = waitForOutputToClear(1) { outputChannel.send(byte) }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun waitAndSend(buffer: ByteArray) = waitForOutputToClear(buffer.size) { outputChannel.send(buffer) }

    override fun write(b: Int) {
        if (buffered.value + 1 >= bufferSize || !outputChannel.trySend(b).isSuccess) {
            waitAndSend(b)
        }
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        val slice = b.copyOfRange(off, off + len)
        if (buffered.value + slice.size >= bufferSize || !outputChannel.trySend(slice).isSuccess) {
            waitAndSend(slice)
        }
    }

    override fun close() {
        super.close()

        outputChannel.close()

        if (closeFlow) {
            runBlocking { flow.close() }
        }
    }

    public suspend fun join(): Unit = job.join()

    private fun init(scope: CoroutineScope) {
        job = scope.launch {
            while (isActive && !outputChannel.isClosedForReceive) {
                when (val value = outputChannel.receive()) {
                    is Number -> {
                        flow.write(value.toInt())
                        buffered -= 1
                    }
                    is ByteArray -> {
                        flow.write(value)
                        buffered -= value.size
                    }

                    //Should never happen
                    else -> {}
                }
                updateChannel.send(Unit)

                delay(50)
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Suppress("FunctionName")
public fun CoroutineScope.FlowOutputStream(
    flow: OutputFlow,
    closeFlow: Boolean,
    bufferSize: Int = 8192,
    channelLimit: Int = bufferSize
): FlowOutputStream =
    FlowOutputStream(this, flow, closeFlow, bufferSize, channelLimit)

@ExperimentalCoroutinesApi
public suspend inline fun <T> CoroutineScope.asOutputStream(
    flow: OutputFlow,
    closeFlow: Boolean,
    bufferSize: Int = 8192,
    channelLimit: Int = bufferSize,
    block: (OutputStream) -> T
): T {
    val stream =
        FlowOutputStream(this, flow, closeFlow, bufferSize, channelLimit)
    val output = BufferedOutputStream(stream).use(block)
    stream.join()
    return output
}