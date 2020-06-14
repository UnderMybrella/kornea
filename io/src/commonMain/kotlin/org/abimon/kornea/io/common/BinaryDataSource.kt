package org.abimon.kornea.io.common

import org.abimon.kornea.io.common.flow.BinaryInputFlow

@ExperimentalUnsignedTypes
public class BinaryDataSource(
    public val byteArray: ByteArray,
    override val maximumInstanceCount: Int? = null,
    override val location: String? = null
) : LimitedInstanceDataSource.Typed<BinaryInputFlow, BinaryDataSource>(withBareOpener(this::openBareLimitedInputFlow)) {
    public companion object {
        @Suppress("RedundantSuspendModifier")
        public suspend fun openBareLimitedInputFlow(self: BinaryDataSource, location: String?): BinaryInputFlow =
            BinaryInputFlow(self.byteArray, location = location ?: self.location)
    }

    override val dataSize: ULong
        get() = byteArray.size.toULong()

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)
}