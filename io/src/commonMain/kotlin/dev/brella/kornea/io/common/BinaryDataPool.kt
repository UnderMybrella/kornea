package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.flow.BinaryInputFlow
import dev.brella.kornea.io.common.flow.BinaryOutputFlow
import dev.brella.kornea.io.common.flow.OutputFlowByDelegate
import dev.brella.kornea.toolkit.common.ObservableDataCloseable

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
public expect class BinaryDataPool : DataPool<BinaryInputFlow, OutputFlowByDelegate<BinaryOutputFlow>>,
    LimitedInstanceDataSource.Typed<BinaryInputFlow, BinaryDataPool> {
    public companion object {
        @PublishedApi
        /**
         * "Why do we declare invoke as an expected function *and* as a extension?"
         *
         * Excellent question!
         *
         * The problem is, in scenarios where the common source set is *also* able to reference coroutineMain,
         * it seems to prioritise the actual declaration *not* the expect declaration, and doesn't recognise the defaults
         *
         * This is a pain in the ass
         *
         */
        internal suspend operator fun invoke(
            location: String?,
            maximumInstanceCount: Int?,
            maximumPermitCount: Int
        ): BinaryDataPool
    }

    override val dataSize: ULong?
    override val reproducibility: DataSourceReproducibility
    internal var outputClosed: Boolean

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow>

    @Suppress("RedundantSuspendModifier")
    internal suspend fun onOutputClosed(closeable: ObservableDataCloseable)
    override suspend fun openOutputFlow(): KorneaResult<OutputFlowByDelegate<BinaryOutputFlow>>
    override suspend fun canOpenOutputFlow(): Boolean
}

@ExperimentalKorneaToolkit
public suspend inline operator fun BinaryDataPool.Companion.invoke(
    location: String? = null,
    maximumInstanceCount: Int? = null,
    maximumPermitCount: Int = 8
): BinaryDataPool = invoke(location, maximumInstanceCount, maximumPermitCount)