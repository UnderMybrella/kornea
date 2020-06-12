package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.annotations.ExperimentalKorneaIO
import org.abimon.kornea.errors.common.KorneaResult
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkClosed
import org.abimon.kornea.io.common.DataSink.Companion.korneaTooManySinksOpen
import org.abimon.kornea.io.common.DataSink.Companion.korneaSinkUnknown
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList

@ExperimentalUnsignedTypes
@ExperimentalKorneaIO
class AsyncFileDataSink(val backing: Path, backingChannel: AsynchronousFileChannel? = null, append: Boolean = false) :
    DataSink<AsyncFileOutputFlow> {
    companion object {
        suspend fun open(
            path: Path,
            executor: ExecutorService? = null,
            append: Boolean = false,
            truncate: Boolean = true,
            create: Boolean = true,
            createNew: Boolean = false,
            deleteOnClose: Boolean = false,
            sparse: Boolean = false,
            sync: Boolean = false,
            dsync: Boolean = false
        ): AsyncFileDataSink = AsyncFileDataSink(path, withContext(Dispatchers.IO) {
            openAsynchronousFileChannel(
                path, executor,
                read = false,
                write = true,
                append = append,
                truncate = truncate,
                create = create,
                createNew = createNew,
                deleteOnClose = deleteOnClose,
                sparse = sparse,
                sync = sync,
                dsync = dsync
            )
        }, append)
    }

    constructor(
        backing: File,
        backingChannel: AsynchronousFileChannel? = null,
        append: Boolean = false
    ) : this(backing.toPath(), backingChannel, append)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<AsyncFileOutputFlow> = ArrayList(1)
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    private var initialised: Boolean = false
    private val channel: AsynchronousFileChannel by lazy {
        initialised = true
        backingChannel ?: AsynchronousFileChannel.open(
            backing,
            listOfNotNull(
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                if (append) StandardOpenOption.APPEND else null
            ).toSet(),
            null
        )
    }

    private suspend fun getChannel(): AsynchronousFileChannel =
        if (!initialised) withContext(Dispatchers.IO) { channel } else channel

    override suspend fun openOutputFlow(): KorneaResult<AsyncFileOutputFlow> =
        when {
            closed -> korneaSinkClosed()
            openInstances.isNotEmpty() -> korneaTooManySinksOpen(1)

            canOpenOutputFlow() -> {
                val stream = AsyncFileOutputFlow(getChannel(), false, backing)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                KorneaResult.success(stream)
            }
            else -> korneaSinkUnknown()
        }

    override suspend fun canOpenOutputFlow(): Boolean = !closed && (openInstances.size < 1)

    @Suppress("RedundantSuspendModifier")
    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is AsyncFileOutputFlow) {
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