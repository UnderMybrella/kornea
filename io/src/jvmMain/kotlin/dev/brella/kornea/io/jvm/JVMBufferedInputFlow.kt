package dev.brella.kornea.io.jvm

import dev.brella.kornea.io.common.flow.BinaryPipeFlow
import dev.brella.kornea.io.common.flow.BufferedInputFlow
import dev.brella.kornea.io.common.flow.invoke
import kotlinx.coroutines.*
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public sealed class JVMBufferStrategy {
    public object GREEDY : JVMBufferStrategy()
    public data class CHUNKED(public val chunkSize: Int) : JVMBufferStrategy()
}

public open class JVMBufferedInputFlow(
    protected val stream: InputStream,
    protected val pipe: BinaryPipeFlow = BinaryPipeFlow(),
    override val location: String? = null,
    public val bufferStrategy: JVMBufferStrategy = JVMBufferStrategy.GREEDY,
    jobScope: CoroutineScope,
    jobContext: CoroutineContext = EmptyCoroutineContext,
    jobDelay: Long = 100L
) : BufferedInputFlow.Sink(pipe.input) {
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    protected val streamJob: Job = jobScope.launch(jobContext) {
        val buffer = ByteArray(8192)
        var read: Int

        when (bufferStrategy) {
            is JVMBufferStrategy.GREEDY ->
                while (isActive && !isClosed) {
                    read = runInterruptible { stream.read(buffer) }
                    pipe.write(buffer, 0, read)

                    yield()
                    delay(jobDelay)
                }

            is JVMBufferStrategy.CHUNKED ->
                while (isActive && !isClosed) {
                    read = runInterruptible { stream.read(buffer) }
                    pipe.write(buffer, 0, read)

                    yield()
                    delay(jobDelay)
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