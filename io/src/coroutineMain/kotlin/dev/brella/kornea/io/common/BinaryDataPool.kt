package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.DataSink.Companion.ERRORS_SINK_CLOSED
import dev.brella.kornea.io.common.DataSink.Companion.korneaSinkClosed
import dev.brella.kornea.io.common.flow.BinaryInputFlow
import dev.brella.kornea.io.common.flow.BinaryOutputFlow
import dev.brella.kornea.io.common.flow.OutputFlowByDelegate
import dev.brella.kornea.io.coroutine.flow.MultiViewOutputFlow
import dev.brella.kornea.toolkit.common.ObservableDataCloseable
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.SharedStateRW
import dev.brella.kornea.toolkit.coroutines.SynchronisedBinaryView

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
public actual class BinaryDataPool(
    override val location: String? = null,
    override val maximumInstanceCount: Int? = null,
    private val output: BinaryOutputFlow,
    private val outputSemaphore: ReadWriteSemaphore,
    private val outputInstanceCount: SharedStateRW<Int>,
) : DataPool<BinaryInputFlow, OutputFlowByDelegate<BinaryOutputFlow>>,
    LimitedInstanceDataSource.Typed<BinaryInputFlow, BinaryDataPool>(withBareOpener(this::openBareLimitedInputFlow)) {
    public actual companion object {
        public actual suspend operator fun invoke(
            location: String?,
            maximumInstanceCount: Int?,
            maximumPermitCount: Int
        ): BinaryDataPool {
            val output = BinaryOutputFlow()
            val dataPool = BinaryDataPool(location, maximumInstanceCount, output, ReadWriteSemaphore(maximumPermitCount), SharedStateRW(0))
            output.registerCloseHandler(dataPool::onOutputClosed)
            return dataPool
        }

        public suspend operator fun invoke(
            location: String? = null,
            maximumInstanceCount: Int? = null,
            output: BinaryOutputFlow,
            outputSemaphore: ReadWriteSemaphore,
            outputInstanceCount: SharedStateRW<Int>
        ): BinaryDataPool {
            val dataPool = BinaryDataPool(location, maximumInstanceCount, output, outputSemaphore, outputInstanceCount)
            output.registerCloseHandler(dataPool::onOutputClosed)
            return dataPool
        }

        @Suppress("RedundantSuspendModifier")
        public suspend fun openBareLimitedInputFlow(self: BinaryDataPool, location: String?): BinaryInputFlow =
            BinaryInputFlow(SynchronisedBinaryView(self.output, self.outputSemaphore), location = location ?: self.location)
    }

    actual override val dataSize: ULong?
        get() = output.getDataSize()

    actual override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isDeterministic = true, isRandomAccess = true)

    internal actual var outputClosed: Boolean = false
    override val isClosed: Boolean
        get() = closed || outputClosed

    actual override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> =
        if (outputClosed) korneaSinkClosed() else super.openNamedInputFlow(location)

    @Suppress("RedundantSuspendModifier")
    internal actual suspend fun onOutputClosed(closeable: ObservableDataCloseable) {
        if (closeable is BinaryOutputFlow) {
            outputClosed = true
        }
    }

    actual override suspend fun openOutputFlow(): KorneaResult<OutputFlowByDelegate<BinaryOutputFlow>> =
        if (canOpenOutputFlow()) KorneaResult.success(MultiViewOutputFlow(output, outputSemaphore, outputInstanceCount))
        else KorneaResult.errorAsIllegalState(ERRORS_SINK_CLOSED, "Sink closed")

    actual override suspend fun canOpenOutputFlow(): Boolean = !outputClosed
}