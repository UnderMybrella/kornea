package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.toolkit.common.DataCloseableEventHandler
import dev.brella.kornea.toolkit.common.ReadWriteSemaphore
import dev.brella.kornea.toolkit.common.SynchronisedBinaryView

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public interface PipeFlow<I : InputFlow, O : OutputFlow> : InputFlow, OutputFlowByDelegate<O> {
    public companion object {
        public inline operator fun <I : InputFlow, O : OutputFlow> invoke(input: I, output: O): PipeFlow<I, O> =
            Sink(input, output)
    }

    public data class Sink<I : InputFlow, O : OutputFlow>(override val input: I, override val output: O) : PipeFlow<I, O>,
        BaseDataCloseable(), InputFlow by input, OutputFlow by output {
        override val isClosed: Boolean
            get() = super<BaseDataCloseable>.isClosed

        override val closeHandlers: List<DataCloseableEventHandler>
            get() = super<BaseDataCloseable>.closeHandlers

        override suspend fun close() {
            super<BaseDataCloseable>.close()
        }

        override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean {
            return super<BaseDataCloseable>.registerCloseHandler(handler)
        }
    }

    public val input: I
    public override val output: O
}

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public inline fun BinaryPipeFlow(location: String? = null): PipeFlow<BinaryInputFlow, OutputFlowByDelegate<BinaryOutputFlow>> {
    val semaphore = ReadWriteSemaphore(1)
    val pipe = BinaryOutputFlow()

    return PipeFlow(
        BinaryInputFlow(SynchronisedBinaryView(pipe, semaphore), location = location),
        SynchronisedOutputFlow(BinaryOutputFlow(), semaphore)
    )
}