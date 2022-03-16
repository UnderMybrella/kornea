package dev.brella.kornea.io.common

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import dev.brella.kornea.io.common.flow.WindowedInputFlow

public open class WindowedDataSource(
    protected val parent: DataSource<*>,
    public val windowOffset: ULong,
    public val windowSize: ULong,
    override val maximumInstanceCount: Int? = (if (parent is LimitedInstanceDataSource<*, *>) parent.maximumInstanceCount else null),
    public val closeParent: Boolean = true,
    override val location: String? =
        "${parent.location}[${windowOffset.toString(16).uppercase()}h,${
            windowOffset.plus(windowSize).toString(16)
                .uppercase()
        }h]"
) : LimitedInstanceDataSource.Typed<WindowedInputFlow, WindowedDataSource>(withLimitedOpener(this::openLimitedInputFlow)) {
    public companion object {
        public suspend fun openLimitedInputFlow(
            self: WindowedDataSource,
            location: String?
        ): KorneaResult<WindowedInputFlow> =
            self.parent.openInputFlow().map { parentFlow ->
                if (parentFlow is SeekableInputFlow) WindowedInputFlow.Seekable(
                    parentFlow,
                    self.windowOffset,
                    self.windowSize,
                    location ?: self.location
                )
                else WindowedInputFlow(parentFlow, self.windowOffset, self.windowSize, location ?: self.location)
            }
    }

    override val dataSize: ULong?
        get() = parent.dataSize?.minus(windowOffset)?.coerceAtMost(windowSize)

    override val reproducibility: DataSourceReproducibility
        get() = parent.reproducibility or DataSourceReproducibility.DETERMINISTIC_MASK

    override fun locationAsUri(): KorneaResult<Uri> = parent.locationAsUri()

    override suspend fun canOpenInputFlow(): Boolean =
        parent.canOpenInputFlow() && super.canOpenInputFlow()

    override suspend fun whenClosed() {
        super.whenClosed()

        if (closeParent) {
            parent.close()
        }
    }
}