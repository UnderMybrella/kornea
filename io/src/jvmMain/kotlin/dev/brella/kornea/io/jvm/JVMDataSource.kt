package dev.brella.kornea.io.jvm

import dev.brella.kornea.io.common.*
import java.io.InputStream

@ExperimentalUnsignedTypes
public class JVMDataSource(
    private val func: () -> InputStream,
    override val maximumInstanceCount: Int? = null,
    override val location: String? = null
) : LimitedInstanceDataSource.Typed<JVMInputFlow, JVMDataSource>(withBareOpener(this::openBareInputFlow)) {
    public companion object {
        @Suppress("RedundantSuspendModifier")
        public suspend fun openBareInputFlow(self: JVMDataSource, location: String?): JVMInputFlow =
            JVMInputFlow(self.func(), location ?: self.location)
    }

    override val dataSize: ULong? = null

    /**
     * The reproducibility traits of this data source.
     *
     * These traits *may* change between invocations, so a fresh instance should be obtained each time
     */
    override val reproducibility: DataSourceReproducibility
        get() = DataSourceReproducibility(isUnreliable = true)
}