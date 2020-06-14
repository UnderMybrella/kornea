package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.annotations.ExperimentalKorneaIO
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList

@ExperimentalKorneaIO
@ExperimentalUnsignedTypes
public class AsyncFileDataPool(
    public val path: Path,
    private val channel: AsynchronousFileChannel? = null,
    append: Boolean = false,
    private val sinkBacker: DataSink<AsyncFileOutputFlow> = AsyncFileDataSink(path, channel, append),
    private val sourceBacker: DataSource<AsyncFileInputFlow> = AsyncFileDataSource(path, channel)
) : DataPool<AsyncFileInputFlow, AsyncFileOutputFlow>,
    DataSink<AsyncFileOutputFlow> by sinkBacker,
    DataSource<AsyncFileInputFlow> by sourceBacker,
    BaseDataCloseable() {
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
        ): AsyncFileDataPool = AsyncFileDataPool(path, withContext(Dispatchers.IO) {
            openAsynchronousFileChannel(
                path, executor,
                read = true,
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
        file: File,
        channel: AsynchronousFileChannel? = null,
        append: Boolean = false,
        sinkBacker: DataSink<AsyncFileOutputFlow> = AsyncFileDataSink(file),
        sourceBacker: DataSource<AsyncFileInputFlow> = AsyncFileDataSource(file)
    ) : this(file.toPath(), channel, append, sinkBacker, sourceBacker)

    override val isClosed: Boolean
        get() = super.isClosed

    override val closeHandlers: List<DataCloseableEventHandler>
        get() = super.closeHandlers

    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean =
        super.registerCloseHandler(handler)

    override suspend fun close() {
        super.close()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        sinkBacker.close()
        sourceBacker.close()

        withContext(Dispatchers.IO) { channel?.close() }
    }
}