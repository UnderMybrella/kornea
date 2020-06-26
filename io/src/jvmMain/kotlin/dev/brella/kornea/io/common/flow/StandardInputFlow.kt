package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.BinaryDataPool
import dev.brella.kornea.io.common.DataCloseableEventHandler
import dev.brella.kornea.io.common.DataPool
import kotlinx.coroutines.*

@ExperimentalUnsignedTypes
public actual class StandardInputFlow(private val bridgeOut: OutputFlow, private val bridgeIn: InputFlow, override val location: String? = "stdin") : BaseDataCloseable(), InputFlow by bridgeIn {
    public companion object {
        @ExperimentalKorneaToolkit
        public suspend operator fun invoke(location: String? = "stdin"): StandardInputFlow = invoke(
            BinaryDataPool(), location
        )

        public suspend operator fun invoke(
            pool: DataPool<*, OutputFlow>,
            location: String? = "stdin"
        ): StandardInputFlow {
            val outFlow = pool.openOutputFlow().get()
            val inFlow = pool.openInputFlow().get()

            return StandardInputFlow(outFlow, inFlow, location)
        }
    }

    private val collector: Job = GlobalScope.launch {
        val stdin = System.`in`
        val buffer = ByteArray(8192)
        var read: Int

        while (isActive) {
            yield()
            read = runInterruptible(Dispatchers.IO) { stdin.read(buffer) }
            bridgeOut.write(buffer, 0, read)
        }
    }

    override val closeHandlers: List<DataCloseableEventHandler>
        get() = super.closeHandlers
    override val isClosed: Boolean
        get() = super.isClosed

    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean {
        return super.registerCloseHandler(handler)
    }

    override suspend fun close() {
        super<BaseDataCloseable>.close()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        collector.cancel()
    }
}