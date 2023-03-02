package dev.brella.kornea.io.jvm

import dev.brella.kornea.io.common.flow.BinaryInputFlow
import dev.brella.kornea.io.common.flow.BinaryOutputFlow
import dev.brella.kornea.io.common.flow.BufferedInputFlow
import dev.brella.kornea.toolkit.coroutines.SynchronisedBinaryView
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public sealed class JVMBufferStrategy {
    public object GREEDY : JVMBufferStrategy()
    public data class CHUNKED(public val chunkSize: Int) : JVMBufferStrategy()
}

public open class JVMBufferedInputFlow(
    protected val stream: InputStream,
    protected val pipe: BinaryOutputFlow = BinaryOutputFlow(),
    protected val pipeSemaphore: Semaphore = Semaphore(1),
    override val location: String? = null,
    public val bufferStrategy: JVMBufferStrategy = JVMBufferStrategy.GREEDY,
    jobScope: CoroutineScope,
    jobContext: CoroutineContext = EmptyCoroutineContext,
    jobDelay: Long = 100L
) : BufferedInputFlow.Sink(BinaryInputFlow(SynchronisedBinaryView(pipe, pipeSemaphore))) {
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    protected val streamJob: Job = jobScope.launch(jobContext) {
        val buffer = ByteArray(8192)
        var read: Int

        when (bufferStrategy) {
            is JVMBufferStrategy.GREEDY ->
                while (isActive && !isClosed) {
                    read = runInterruptible { stream.read(buffer) }
                    pipeSemaphore.withPermit { pipe.write(buffer, 0, read) }

                    yield()
                    delay(jobDelay)
                }

            is JVMBufferStrategy.CHUNKED -> {
                val chunkSize = bufferStrategy.chunkSize.toUInt()
                while (isActive && !isClosed) {
                    if (pipe.getDataSize() < chunkSize) {
                        read = runInterruptible { stream.read(buffer) }
                        pipeSemaphore.withPermit { pipe.write(buffer, 0, read) }
                    }

                    yield()
                    delay(jobDelay)
                }
            }
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        streamJob.cancelAndJoin()
        runInterruptible(Dispatchers.IO) { stream.close() }
    }

    init {
        if (stream.markSupported()) {
            stream.mark(Int.MAX_VALUE)
        }
    }
}

public inline fun CoroutineScope.asBufferedInputFlow(
    stream: InputStream,
    context: CoroutineContext = EmptyCoroutineContext,
    strategy: JVMBufferStrategy = JVMBufferStrategy.GREEDY
): JVMBufferedInputFlow = JVMBufferedInputFlow(stream, jobScope = this, jobContext = context, bufferStrategy = strategy)