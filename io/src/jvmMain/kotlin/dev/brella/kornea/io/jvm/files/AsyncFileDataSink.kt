package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.annotations.ExperimentalKorneaIO
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.DataSink
import dev.brella.kornea.io.common.DataSink.Companion.korneaSinkClosed
import dev.brella.kornea.io.common.DataSink.Companion.korneaSinkUnknown
import dev.brella.kornea.io.common.DataSink.Companion.korneaTooManySinksOpen
import dev.brella.kornea.io.common.ObservableDataCloseable
import dev.brella.kornea.io.common.closeAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.ExecutorService

@ExperimentalUnsignedTypes
@ExperimentalKorneaIO
public class AsyncFileDataSink(public val backing: Path, backingChannel: AsynchronousFileChannel? = null, append: Boolean = false) :
    BaseDataCloseable(), DataSink<AsyncFileOutputFlow> {
    public companion object {
        public suspend fun open(
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
        ): AsyncFileDataSink =
            AsyncFileDataSink(path, runInterruptible(Dispatchers.IO) {
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

    public constructor(
        backing: File,
        backingChannel: AsynchronousFileChannel? = null,
        append: Boolean = false
    ) : this(backing.toPath(), backingChannel, append)

    private val openInstances: MutableList<AsyncFileOutputFlow> = ArrayList(1)

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
        if (!initialised) runInterruptible(Dispatchers.IO) { channel } else channel

    override suspend fun openOutputFlow(): KorneaResult<AsyncFileOutputFlow> =
        when {
            closed -> korneaSinkClosed()
            openInstances.isNotEmpty() -> korneaTooManySinksOpen(1)

            canOpenOutputFlow() -> {
                val stream =
                    AsyncFileOutputFlow(getChannel(), false, backing)
                stream.registerCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                KorneaResult.success(stream)
            }
            else -> korneaSinkUnknown()
        }

    override suspend fun canOpenOutputFlow(): Boolean = !closed && (openInstances.isEmpty())

    @Suppress("RedundantSuspendModifier")
    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is AsyncFileOutputFlow) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        openInstances.closeAll()
        openInstances.clear()
    }
}