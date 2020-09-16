package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.PeekableInputFlow
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import dev.brella.kornea.io.jvm.*
import dev.brella.kornea.toolkit.common.*
import dev.brella.kornea.toolkit.coroutines.pools.KorneaPool
import dev.brella.kornea.toolkit.coroutines.pools.KorneaPools
import dev.brella.kornea.toolkit.coroutines.pools.PoolableWrapper
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.lang.Integer.min
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KMutableProperty0

@ExperimentalUnsignedTypes
@ExperimentalKorneaToolkit
public class AsyncFileInputFlow private constructor(
    private val channel: AsynchronousFileChannel,
    private val localChannel: Boolean,
    public val backing: Path,
    override val location: String?
) : BaseDataCloseable(), PeekableInputFlow, SeekableInputFlow, SuspendInit0 {
    public companion object {
        public const val DEFAULT_BUFFER_SIZE: Int = 8192

        private val THREAD_POOL: KorneaPool<CoroutineContext> = KorneaPools.newCachedPool(Int.MAX_VALUE) {
            PoolableWrapper(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
        }

        public suspend operator fun invoke(
            channel: AsynchronousFileChannel,
            localChannel: Boolean,
            backing: Path,
            location: String?
        ): AsyncFileInputFlow =
            init(AsyncFileInputFlow(channel, localChannel, backing, location))

        public suspend operator fun invoke(
            backingPath: Path,
            location: String? = backingPath.toString()
        ): AsyncFileInputFlow =
            init(AsyncFileInputFlow(backingPath, location))

        public suspend operator fun invoke(
            backingFile: File,
            location: String? = backingFile.toString()
        ): AsyncFileInputFlow =
            init(AsyncFileInputFlow(backingFile, location))
    }

    private constructor(backingPath: Path, location: String? = backingPath.toString()) : this(
        AsynchronousFileChannel.open(
            backingPath,
            StandardOpenOption.READ
        ), true, backingPath, location
    )

    private constructor(backingFile: File, location: String? = backingFile.toString()) : this(
        backingFile.toPath(),
        location
    )

    //    private var coroutineContext: Poolable<CoroutineContext>? = null
    private val mutex: Mutex = Mutex()

    private var flowFilePointer: Long = 0L
    private var buffer: ByteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE).apply { limitSafe(0) } //Force a refill
    private var peekFilePointer: Long = 0L
    private var peekBuffer: ByteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE).apply { limitSafe(0) }

    private suspend inline fun ByteBuffer.fill(
        moveFilePointer: Boolean = true,
        filePointer: KMutableProperty0<Long> = ::flowFilePointer
    ): Int? =
        if (!isClosed) {
            buffer.positionSafe(0).limitSafe(0)
            fillPartial(moveFilePointer, filePointer)
        } else {
            null
        }

    private suspend fun ByteBuffer.fillPartial(
        moveFilePointer: Boolean = true,
        filePointer: KMutableProperty0<Long> = ::flowFilePointer
    ): Int? {
        if (!isClosed) {
            limitSafe(capacity())
            val pos = position()
            val n = channel.readAwaitOrNull(this, filePointer.get())
            limitSafe(position())
            positionSafe(pos)

            if (n == null || n < 0) {
                return null
            } else if (n > 0) {
                if (moveFilePointer) filePointer.set(filePointer.get() + n)

                return n
            }

            return 0
        } else {
            return null
        }
    }

    override suspend fun read(): Int? =
        mutex.withLock {
            if (!buffer.hasRemaining()) {
                buffer.fill() ?: return@withLock null

                if (!buffer.hasRemaining()) return@withLock null
            }

            return@withLock buffer.get().asInt()
        }

    private suspend fun read1(b: ByteArray, off: Int, len: Int): Int? {
        var avail = buffer.remaining()
        if (avail <= 0) {
            if (len >= buffer.capacity()) {
                val bytebuf = ByteBuffer.wrap(b, off, len)
                val n = channel.readAwaitOrNull(bytebuf, flowFilePointer) ?: return null
                if (n > 0) flowFilePointer += n

                return n
            }

            avail = buffer.fill() ?: return null

            if (!buffer.hasRemaining()) return null
        }


        val cnt = if (avail < len) avail else len
        buffer.get(b, off, cnt) //LEAVE THIS IN BRELLA
        return cnt
    }

    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? =
        mutex.withLock {
            if ((off or len or (off + len) or (b.size - (off + len))) < 0) {
                throw IndexOutOfBoundsException()
            } else if (len == 0) {
                return@withLock 0
            }

            var n = 0

            while (n < len) {
                val nread = read1(b, off + n, len - n) ?: return@withLock if (n > 0) n else null
                if (nread <= 0) return@withLock if (n == 0) nread else n
                n += nread
                yield()
            }

            return@withLock n
        }

    public suspend fun readAt(b: ByteArray, off: Int, len: Int, filePointer: Long): Int? =
        mutex.withLock { channel.readAwaitOrNull(ByteBuffer.wrap(b, off, len), filePointer) }

    override suspend fun peek(forward: Int): Int? =
        mutex.withLock {
            loopAtMostTwice {
                val bufferForward = buffer.position() + forward

                if (bufferForward < buffer.limit()) {
                    //Within the main buffer
                    return@withLock buffer.get(buffer.position() + forward - 1).asInt()
                } else if (flowFilePointer - buffer.limit() + forward in peekFilePointer - peekBuffer.limit() until peekFilePointer) {
                    //Within the peek buffer
                    return@withLock peekBuffer.get((flowFilePointer - buffer.limit() + forward - peekFilePointer + peekBuffer.limit()).toInt())
                        .asInt()
                } else if (!buffer.hasRemaining()) {
                    buffer.fill()

                    return@loopAtMostTwice
                } else if (!peekBuffer.hasRemaining()) {
                    peekBuffer.fill()

                    return@loopAtMostTwice
                } else if (bufferForward < buffer.capacity()) {
                    //Shuffle down
                    val tmp = ByteArray(buffer.capacity() - buffer.position())
                    buffer.get(tmp)
                    buffer.clearSafe()
                    buffer.put(tmp)
                    buffer.fillPartial()
                    buffer.positionSafe(0)

                    return@loopAtMostTwice
                } else {
                    return@withLock null
                }
            }

            null
        }

    override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? =
        mutex.withLock {
            loopAtMostTwice {
                val bufferStartPos = (flowFilePointer - buffer.limit())
                val peekStartPos = (peekFilePointer - peekBuffer.limit())

                val bufferStart = buffer.position() + forward
                val bufferForward = bufferStart + len

                val bufferAbsStart = bufferStartPos + forward
                val bufferAbsForward = bufferAbsStart + len
                val peekRange = peekStartPos until peekFilePointer

                if (bufferAbsStart in peekRange && bufferAbsForward in peekRange) {
                    //Within the peek buffer
                    return@withLock peekBuffer.bookmark {
                        positionSafe((bufferAbsStart - peekStartPos).toInt())
                        get(b, off, min(buffer.limit() - bufferStart, len))
                        len
                    }
                } else if (bufferStart < buffer.limit()) {
                    //Within the main buffer
                    return@withLock buffer.bookmark {
                        positionSafe(bufferStart)
                        get(b, off, min(buffer.limit() - bufferStart, len))
                        len
                    }
                } else if (!buffer.hasRemaining()) {
                    buffer.fill()

                    return@loopAtMostTwice
                } else if (!peekBuffer.hasRemaining()) {
                    peekBuffer.fill()

                    return@loopAtMostTwice
                } else if (bufferForward < buffer.capacity()) {
                    //Shuffle down
                    val tmp = ByteArray(buffer.capacity() - buffer.position())
                    buffer.get(tmp)
                    buffer.clearSafe()
                    buffer.put(tmp)
                    buffer.fillPartial()
                    buffer.positionSafe(0)

                    return@loopAtMostTwice
                }
            }

            null
        }

    override suspend fun skip(n: ULong): ULong =
        mutex.withLock {
            if (!buffer.hasRemaining()) {
                flowFilePointer += n.toLong()
                return@withLock n
            }

            if (buffer.remaining() < n.toInt()) {
                val avail = buffer.remaining()
                buffer.positionSafe(buffer.capacity())
                flowFilePointer += n.toInt() - avail

                return@withLock n
            } else {
                buffer.positionSafe(buffer.position() + n.toInt())

                return@withLock n
            }
        }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = runInterruptible(Dispatchers.IO) { channel.size().toULong() }
    override suspend fun position(): ULong =
        mutex.withLock { flowFilePointer - buffer.limit() + buffer.position() }.toULong()

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong =
        mutex.withLock {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    if (pos in (flowFilePointer - buffer.limit()) until flowFilePointer) {
                        buffer.positionSafe((pos - (flowFilePointer - buffer.limit())).toInt())
                    } else {
                        flowFilePointer = pos
                        buffer.positionSafe(0).limitSafe(0)
                    }
                }
                EnumSeekMode.FROM_POSITION -> {
                    if (buffer.position() + pos in 0..buffer.limit()) {
                        buffer.positionSafe((buffer.position() + pos).toInt())
                    } else {
                        flowFilePointer += buffer.position() + pos - buffer.limit()
                        buffer.positionSafe(0).limitSafe(0)
                    }
                }
                EnumSeekMode.FROM_END -> {
                    val pos = size().toLong() - pos
                    if (pos in (flowFilePointer - buffer.limit()) until flowFilePointer) {
                        buffer.positionSafe((pos - (flowFilePointer - buffer.limit())).toInt())
                    } else {
                        flowFilePointer = pos
                        buffer.positionSafe(0).limitSafe(0)
                    }
                }
            }

            this
        }.position()

    override suspend fun init() {
//        coroutineContext = THREAD_POOL.hire().get()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        if (localChannel) {
            mutex.withLock { runInterruptible(Dispatchers.IO) { channel.close() } }
        }

//        THREAD_POOL.retire(coroutineContext!!)
    }
}