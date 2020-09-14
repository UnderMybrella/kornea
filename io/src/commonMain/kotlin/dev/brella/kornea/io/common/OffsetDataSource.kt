package dev.brella.kornea.io.common

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import dev.brella.kornea.io.common.flow.SinkOffsetInputFlow

@ExperimentalUnsignedTypes
public open class OffsetDataSource(
    protected val parent: DataSource<*>,
    public val offset: ULong,
    override val maximumInstanceCount: Int? = (if (parent is LimitedInstanceDataSource<*, *>) parent.maximumInstanceCount else null),
    public val closeParent: Boolean = true,
    override val location: String? = "${parent.location}+${offset.toString(16).toUpperCase()}h"
) : LimitedInstanceDataSource.Typed<SinkOffsetInputFlow, OffsetDataSource>(withLimitedOpener(this::openLimitedInputFlow)) {
    public companion object {
        @Suppress("RedundantSuspendModifier")
        public suspend fun openLimitedInputFlow(self: OffsetDataSource, location: String?): KorneaResult<SinkOffsetInputFlow> =
            self.parent.openInputFlow().map { parentFlow ->
                if (parentFlow is SeekableInputFlow) SinkOffsetInputFlow.Seekable(parentFlow, self.offset, location ?: self.location)
                else SinkOffsetInputFlow(parentFlow, self.offset, location ?: self.location)
            }
    }

    override val dataSize: ULong?
        get() = parent.dataSize?.minus(offset)

    override val reproducibility: DataSourceReproducibility
        get() = parent.reproducibility or DataSourceReproducibility.DETERMINISTIC_MASK

    override suspend fun canOpenInputFlow(): Boolean =
        parent.canOpenInputFlow() && super.canOpenInputFlow()

    override suspend fun whenClosed() {
        super.whenClosed()

        if (closeParent) {
            parent.close()
        }
    }
}