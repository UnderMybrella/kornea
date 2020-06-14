package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.io.common.DataSourceReproducibility
import org.abimon.kornea.io.common.LimitedInstanceDataSource
import java.io.File

@ExperimentalUnsignedTypes
public class SynchronousFileDataSource(
    public val backing: File,
    override val maximumInstanceCount: Int? = null,
    override val location: String? = backing.absolutePath
) : LimitedInstanceDataSource.Typed<SynchronousFileInputFlow, SynchronousFileDataSource>(withBareOpener(this::openBareLimitedInputFlow)) {

    public companion object {
        public suspend fun openBareLimitedInputFlow(
            self: SynchronousFileDataSource,
            location: String?
        ): SynchronousFileInputFlow =
            SynchronousFileInputFlow(self.backing, location ?: self.location)
    }

    override val dataSize: ULong
        get() = backing.length().toULong()

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)
}