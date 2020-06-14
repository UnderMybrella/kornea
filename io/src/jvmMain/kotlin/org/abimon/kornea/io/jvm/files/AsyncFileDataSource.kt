package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.annotations.BlockingOperation
import org.abimon.kornea.annotations.ExperimentalKorneaIO
import org.abimon.kornea.io.common.*
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@ExperimentalUnsignedTypes
@ExperimentalKorneaIO
public class AsyncFileDataSource(
    public val backing: Path,
    backingChannel: AsynchronousFileChannel? = null,
    public val localChannel: Boolean = backingChannel == null,
    override val maximumInstanceCount: Int? = null,
    override val location: String? = backing.toString()
) : LimitedInstanceDataSource.Typed<AsyncFileInputFlow, AsyncFileDataSource>(withBareOpener(this::openBareLimitedInputFlow)) {

    public companion object {
        public suspend fun openBareLimitedInputFlow(self: AsyncFileDataSource, location: String?): AsyncFileInputFlow =
            AsyncFileInputFlow(self.getChannel(), false, self.backing, location ?: self.location)
    }

    public constructor(
        backing: File,
        backingChannel: AsynchronousFileChannel? = null,
        localChannel: Boolean = backingChannel == null,
        maximumInstanceCount: Int? = null,
        location: String? = backing.absolutePath
    ) : this(backing.toPath(), backingChannel, localChannel, maximumInstanceCount, location)

    override val dataSize: ULong
        @BlockingOperation
        get() = Files.size(backing).toULong()

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    private var initialised: Boolean = backingChannel != null
    private val channel: AsynchronousFileChannel by lazy {
        initialised = true
        backingChannel ?: AsynchronousFileChannel.open(backing, StandardOpenOption.READ)
    }

    private suspend fun getChannel(): AsynchronousFileChannel =
        if (!initialised) withContext(Dispatchers.IO) { channel } else channel

    override suspend fun whenClosed() {
        super.whenClosed()

        if (localChannel) withContext(Dispatchers.IO) { channel.close() }
    }
}