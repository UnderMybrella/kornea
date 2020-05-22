package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.erorrs.common.korneaNotFound
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceClosed
import org.abimon.kornea.io.common.DataSource.Companion.korneaSourceUnknown
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.math.max

@ExperimentalUnsignedTypes
@ExperimentalKorneaIO
class AsyncFileDataSource(val backing: Path, backingChannel: AsynchronousFileChannel? = null, val localChannel: Boolean = backingChannel == null, val maxInstanceCount: Int = -1, override val location: String? = backing.toString()): DataSource<AsyncFileInputFlow> {
    constructor(backing: File, backingChannel: AsynchronousFileChannel? = null, localChannel: Boolean = backingChannel == null, maxInstanceCount: Int = -1, location: String? = backing.absolutePath): this(backing.toPath(), backingChannel, localChannel, maxInstanceCount, location)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    override val dataSize: ULong
        @BlockingOperation
        get() = Files.size(backing).toULong()

    private val openInstances: MutableList<AsyncFileInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility = DataSourceReproducibility(isStatic = true, isRandomAccess = true)
    private var initialised: Boolean = backingChannel != null
    private val channel: AsynchronousFileChannel by lazy {
        initialised = true
        backingChannel ?: AsynchronousFileChannel.open(backing, StandardOpenOption.READ)
    }

    private suspend fun getChannel(): AsynchronousFileChannel =
        if (!initialised) withContext(Dispatchers.IO) { channel } else channel

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<AsyncFileInputFlow> =
        when {
            closed -> korneaSourceClosed()
            !initialised && !Files.exists(backing) -> korneaNotFound("$backing does not exist")
            canOpenInputFlow() -> {
                val flow = AsyncFileInputFlow(getChannel(), false, backing, location ?: this.location)
                flow.addCloseHandler(this::instanceClosed)
                openInstances.add(flow)
                KorneaResult.Success(flow)
            }
            else -> korneaSourceUnknown()
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

            if (localChannel) withContext(Dispatchers.IO) { channel.close() }
        }
    }
}