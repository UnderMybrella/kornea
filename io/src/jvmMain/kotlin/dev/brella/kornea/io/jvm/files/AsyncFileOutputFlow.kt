package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.IntFlowState
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.io.common.flow.OutputFlowState
import dev.brella.kornea.io.common.flow.SeekableFlow
import dev.brella.kornea.io.jvm.clearSafe
import dev.brella.kornea.io.jvm.flipSafe
import dev.brella.kornea.io.jvm.limitSafe
import dev.brella.kornea.io.jvm.positionSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.util.concurrent.ExecutorService

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class AsyncFileOutputFlow(
    private val channel: AsynchronousFileChannel,
    private val isLocalChannel: Boolean,
    public val backing: Path,
    override val location: String? = "AsyncFileOutputFlow(${backing.toUri()})"
) : BaseDataCloseable(), OutputFlow, SeekableFlow, OutputFlowState, IntFlowState by IntFlowState.base() {
    public companion object {
        public const val DEFAULT_APPEND: Boolean = false
        public const val DEFAULT_TRUNCATE: Boolean = true
        public const val DEFAULT_CREATE: Boolean = true
        public const val DEFAULT_CREATE_NEW: Boolean = false
        public const val DEFAULT_DELETE_ON_CLOSE: Boolean = false
        public const val DEFAULT_SPARSE: Boolean = false
        public const val DEFAULT_SYNC: Boolean = false
        public const val DEFAULT_DSYNC: Boolean = false

        public suspend fun open(
            path: Path,
            location: String? = null,
            executor: ExecutorService? = null,
            append: Boolean = DEFAULT_APPEND,
            truncate: Boolean = DEFAULT_TRUNCATE,
            create: Boolean = DEFAULT_CREATE,
            createNew: Boolean = DEFAULT_CREATE_NEW,
            deleteOnClose: Boolean = DEFAULT_DELETE_ON_CLOSE,
            sparse: Boolean = DEFAULT_SPARSE,
            sync: Boolean = DEFAULT_SYNC,
            dsync: Boolean = DEFAULT_DSYNC
        ): AsyncFileOutputFlow =
            AsyncFileOutputFlow(runInterruptible(Dispatchers.IO) {
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
            }, append, path, location)
    }

    public constructor(
        path: Path,
        location: String? = null,
        executor: ExecutorService? = null,
        append: Boolean = DEFAULT_APPEND,
        truncate: Boolean = DEFAULT_TRUNCATE,
        create: Boolean = DEFAULT_CREATE,
        createNew: Boolean = DEFAULT_CREATE_NEW,
        deleteOnClose: Boolean = DEFAULT_DELETE_ON_CLOSE,
        sparse: Boolean = DEFAULT_SPARSE,
        sync: Boolean = DEFAULT_SYNC,
        dsync: Boolean = DEFAULT_DSYNC
    ) : this(
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
        ), true, path, location
    )

    public constructor(
        file: File,
        location: String? = null,
        executor: ExecutorService? = null,
        append: Boolean = DEFAULT_APPEND,
        truncate: Boolean = DEFAULT_TRUNCATE,
        create: Boolean = DEFAULT_CREATE,
        createNew: Boolean = DEFAULT_CREATE_NEW,
        deleteOnClose: Boolean = DEFAULT_DELETE_ON_CLOSE,
        sparse: Boolean = DEFAULT_SPARSE,
        sync: Boolean = DEFAULT_SYNC,
        dsync: Boolean = DEFAULT_DSYNC
    ) : this(file.toPath(), location, executor, append, truncate, create, createNew, deleteOnClose, sparse, sync, dsync)

    private var filePointer = 0L
    private val buffer = ByteBuffer.allocate(8192)
    private val mutex = Mutex()

    override suspend fun position(): ULong =
        filePointer.toULong()

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.success(Uri.fromUri(backing.toUri()))

    private suspend fun flushBuffer() {
        if (!closed && buffer.position() != 0) {
            buffer.flipSafe()
            channel.writeAwaitOrNull(buffer, filePointer)?.let { filePointer += it }
            buffer.clearSafe()
        }
    }

    override suspend fun write(byte: Int) {
        if (closed) return
        mutex.withLock {
            flushBuffer()
            buffer.put(byte.toByte())
        }
    }

    override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        if (closed) {
            return
        }

        if ((off or len or (off + len) or (b.size - (off + len))) < 0) {
            throw IndexOutOfBoundsException()
        } else if (len == 0) {
            return
        }

        mutex.withLock {
            if (len >= buffer.capacity()) {
                flushBuffer()
                val byteBuf = ByteBuffer.wrap(b)
                channel.writeAwaitOrNull(byteBuf, filePointer)?.let { filePointer += it }

                return
            }

            if (len > buffer.remaining()) {
                flushBuffer()
            }

            buffer.put(b, off, len)
        }
    }

    override suspend fun flush(): Unit = mutex.withLock { flushBuffer() }

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong =
        mutex.withLock {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    if (pos in (filePointer - buffer.limit()) until filePointer) {
                        buffer.positionSafe((pos - (filePointer - buffer.limit())).toInt())
                    } else {
                        filePointer = pos
                        buffer.positionSafe(0).limitSafe(0)
                    }
                }
                EnumSeekMode.FROM_POSITION -> {
                    if (buffer.position() + pos in 0..buffer.limit()) {
                        buffer.positionSafe((buffer.position() + pos).toInt())
                    } else {
                        filePointer += buffer.position() + pos - buffer.limit()
                        buffer.positionSafe(0).limitSafe(0)
                    }
                }
                EnumSeekMode.FROM_END -> {
                    val pos = runInterruptible { channel.size() } - pos
                    if (pos in (filePointer - buffer.limit()) until filePointer) {
                        buffer.positionSafe((pos - (filePointer - buffer.limit())).toInt())
                    } else {
                        filePointer = pos
                        buffer.positionSafe(0).limitSafe(0)
                    }
                }
            }

//            (filePointer - buffer.limit() + buffer.position()).toULong()
            position()
        }

    override suspend fun whenClosed() {
        super.whenClosed()

        mutex.withLock {
            if (buffer.position() != 0) {
                buffer.flipSafe()
                channel.writeAwaitOrNull(buffer, filePointer)?.let { filePointer += it }
                buffer.clearSafe()
            }

            if (isLocalChannel) {
                runInterruptible(Dispatchers.IO) {
                    channel.force(true)
                    channel.close()
                }
            }
        }
    }
}