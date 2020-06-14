package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.BaseDataCloseable
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.CountingOutputFlow
import org.abimon.kornea.io.jvm.flipSafe
import org.abimon.kornea.io.jvm.rewindSafe
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList

@ExperimentalUnsignedTypes
public class AsyncFileOutputFlow(
    private val channel: AsynchronousFileChannel,
    private val isLocalChannel: Boolean,
    public val backing: Path
) : BaseDataCloseable(), CountingOutputFlow {
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
        ): AsyncFileOutputFlow = AsyncFileOutputFlow(withContext(Dispatchers.IO) {
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
        }, append, path)
    }

    public constructor(path: Path, append: Boolean = false) : this(
        AsynchronousFileChannel.open(
            path,
            listOfNotNull(
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                if (append) StandardOpenOption.APPEND else null
            ).toSet(),
            null
        ), true, path
    )

    public constructor(file: File, append: Boolean = false) : this(file.toPath(), append)

    private var filePointer = 0L
    private val buffer = ByteBuffer.allocate(8192)
    private val mutex = Mutex()

    override val streamOffset: Long
        get() = filePointer

    private suspend fun flushBuffer() {
        if (!closed && buffer.position() != 0) {
            buffer.flipSafe()
            channel.writeAwaitOrNull(buffer, filePointer)?.let { filePointer += it }
            buffer.clear()
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

    override suspend fun whenClosed() {
        super.whenClosed()

        if (isLocalChannel) {
            mutex.withLock {
                flushBuffer()
                withContext(Dispatchers.IO) {
                    channel.force(true)
                    channel.close()
                }
            }
        }
    }
}