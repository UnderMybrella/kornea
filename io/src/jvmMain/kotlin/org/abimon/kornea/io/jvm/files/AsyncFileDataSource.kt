package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.*
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.math.max

@ExperimentalUnsignedTypes
@ExperimentalKorneaIO
class AsyncFileDataSource(val backing: Path, backingChannel: AsynchronousFileChannel? = null, val maxInstanceCount: Int = -1, override val location: String? = backing.toString()): DataSource<AsyncFileInputFlow> {
    constructor(backing: File, backingChannel: AsynchronousFileChannel? = null, maxInstanceCount: Int = -1, location: String? = backing.absolutePath): this(backing.toPath(), backingChannel, maxInstanceCount, location)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    override val dataSize: ULong
        @BlockingOperation
        get() = Files.size(backing).toULong()

    private val openInstances: MutableList<AsyncFileInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility = DataSourceReproducibility(isStatic = true, isRandomAccess = true)
    private var initialised: Boolean = false
    private val channel: AsynchronousFileChannel by lazy {
        initialised = true
        backingChannel ?: AsynchronousFileChannel.open(backing, StandardOpenOption.READ)
    }

    private suspend fun getChannel(): AsynchronousFileChannel =
        if (!initialised) withContext(Dispatchers.IO) { channel } else channel

    override suspend fun openNamedInputFlow(location: String?): AsyncFileInputFlow? {
        if (canOpenInputFlow()) {
            val stream = AsyncFileInputFlow(getChannel(), false, backing, location ?: this.location)
            stream.addCloseHandler(this::instanceClosed)
            openInstances.add(stream)
            return stream
        } else {
            return null
        }
    }

    override suspend fun canOpenInputFlow(): Boolean = !closed && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is AsyncFileInputFlow) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            closed = true
            openInstances.toTypedArray().closeAll()
            openInstances.clear()
        }
    }
}