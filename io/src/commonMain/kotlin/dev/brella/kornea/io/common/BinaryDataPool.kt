package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.DataSink.Companion.ERRORS_SINK_CLOSED
import dev.brella.kornea.io.common.DataSink.Companion.korneaSinkClosed
import dev.brella.kornea.io.common.flow.BinaryInputFlow
import dev.brella.kornea.io.common.flow.BinaryOutputFlow
import dev.brella.kornea.io.common.flow.MultiViewOutputFlow
import dev.brella.kornea.toolkit.common.ObservableDataCloseable
import dev.brella.kornea.toolkit.common.ReadWriteSemaphore
import dev.brella.kornea.toolkit.common.SharedStateRW
import dev.brella.kornea.toolkit.common.SynchronisedBinaryView

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
public class BinaryDataPool(
    override val location: String? = null,
    override val maximumInstanceCount: Int? = null,
    private val output: BinaryOutputFlow,
    private val outputSemaphore: ReadWriteSemaphore,
    private val outputInstanceCount: SharedStateRW<Int>,
) : DataPool<BinaryInputFlow, MultiViewOutputFlow<BinaryOutputFlow>>,
    LimitedInstanceDataSource.Typed<BinaryInputFlow, BinaryDataPool>(withBareOpener(this::openBareLimitedInputFlow)) {
    public companion object {
        public suspend operator fun invoke(
            location: String? = null,
            maximumInstanceCount: Int? = null,
            maximumPermitCount: Int = 8
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

    override val dataSize: ULong?
        get() = output.getDataSize()

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isDeterministic = true, isRandomAccess = true)

    private var outputClosed: Boolean = false
    override val isClosed: Boolean
        get() = closed || outputClosed

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> =
        if (outputClosed) korneaSinkClosed() else super.openNamedInputFlow(location)

    @Suppress("RedundantSuspendModifier")
    private suspend fun onOutputClosed(closeable: ObservableDataCloseable) {
        if (closeable is BinaryOutputFlow) {
            outputClosed = true
        }
    }

    override suspend fun openOutputFlow(): KorneaResult<MultiViewOutputFlow<BinaryOutputFlow>> =
        if (canOpenOutputFlow()) KorneaResult.success(MultiViewOutputFlow.invoke(output, outputSemaphore, outputInstanceCount))
        else KorneaResult.errorAsIllegalState(ERRORS_SINK_CLOSED, "Sink closed")

    override suspend fun canOpenOutputFlow(): Boolean = !outputClosed
}