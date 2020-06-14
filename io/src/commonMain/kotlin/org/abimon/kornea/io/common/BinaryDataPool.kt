package org.abimon.kornea.io.common

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import org.abimon.kornea.annotations.ExperimentalKorneaToolkit
import org.abimon.kornea.errors.common.KorneaResult
import org.abimon.kornea.io.common.DataSink.Companion.ERRORS_SINK_CLOSED
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkClosed
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import org.abimon.kornea.io.common.flow.BinaryOutputFlow
import org.abimon.kornea.io.common.flow.MultiViewOutputFlow
import org.kornea.toolkit.common.SharedStateRW

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
public class BinaryDataPool (
    override val location: String? = null,
    override val maximumInstanceCount: Int? = null,
    private val output: BinaryOutputFlow,
    private val outputMutex: Mutex,
    private val outputInstanceCount: SharedStateRW<Int>,
) : DataPool<BinaryInputFlow, MultiViewOutputFlow<BinaryOutputFlow>>,
    LimitedInstanceDataSource.Typed<BinaryInputFlow, BinaryDataPool>(withBareOpener(this::openBareLimitedInputFlow)) {
    public companion object {
        public suspend operator fun invoke(
            location: String? = null,
            maximumInstanceCount: Int? = null
        ): BinaryDataPool {
            val output = BinaryOutputFlow()
            val dataPool = BinaryDataPool(location, maximumInstanceCount, output, Mutex(), SharedStateRW(0))
            output.registerCloseHandler(dataPool::onOutputClosed)
            return dataPool
        }

        public suspend operator fun invoke(
            location: String? = null,
            maximumInstanceCount: Int? = null,
            output: BinaryOutputFlow,
            outputMutex: Mutex,
            outputInstanceCount: SharedStateRW<Int>
        ): BinaryDataPool {
            val dataPool = BinaryDataPool(location, maximumInstanceCount, output, outputMutex, outputInstanceCount)
            output.registerCloseHandler(dataPool::onOutputClosed)
            return dataPool
        }

        @Suppress("RedundantSuspendModifier")
        public suspend fun openBareLimitedInputFlow(self: BinaryDataPool, location: String?): BinaryInputFlow =
            BinaryInputFlow(self.output.getData(), location = location ?: self.location)
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
        if (canOpenOutputFlow()) KorneaResult.success(MultiViewOutputFlow(output, outputMutex, outputInstanceCount))
        else KorneaResult.errorAsIllegalState(ERRORS_SINK_CLOSED, "Sink closed")

    override suspend fun canOpenOutputFlow(): Boolean = !outputClosed
}