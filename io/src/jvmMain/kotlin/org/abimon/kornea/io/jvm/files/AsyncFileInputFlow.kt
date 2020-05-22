package org.abimon.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.PeekableInputFlow
import org.abimon.kornea.io.common.flow.SeekableInputFlow
import org.abimon.kornea.io.jvm.clearSafe
import org.abimon.kornea.io.jvm.limitSafe
import org.abimon.kornea.io.jvm.positionSafe
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@ExperimentalUnsignedTypes
class AsyncFileInputFlow(
    val channel: AsynchronousFileChannel,
    private val localChannel: Boolean,
    val backing: Path,
    override val location: String?
) : PeekableInputFlow, SeekableInputFlow {
    companion object {
        const val DEFAULT_BUFFER_SIZE = 8192
    }

    constructor(backingPath: Path, location: String? = backingPath.toString()) : this(
        AsynchronousFileChannel.open(
            backingPath,
            StandardOpenOption.READ
        ), true, backingPath, location
    )

    constructor(backingFile: File, location: String? = backingFile.toString()) : this(backingFile.toPath(), location)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val bufferMutex: Mutex = Mutex()
    private var flowFilePointer: Long = 0L
    private var buffer: ByteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE).apply { limitSafe(0) } //Force a refill
    private var peekFilePointer: Long = 0L
    private var peekBuffer: ByteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE).apply { limitSafe(0) }
    private var count: Int = 0

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    private suspend fun fill(moveFilePointer: Boolean = true, filePointer: Long = flowFilePointer): Int? {
        if (!isClosed) {
            buffer.clearSafe()
            return fillPartial(moveFilePointer, filePointer)
        } else {
            return null
        }
    }

    private suspend fun fillPartial(moveFilePointer: Boolean = true, filePointer: Long = flowFilePointer): Int? {
        if (!isClosed) {
            buffer.limitSafe(buffer.capacity())
            val pos = buffer.position()
            val n = channel.readAwaitOrNull(buffer, filePointer) ?: return null
            if (n > 0) {
                if (moveFilePointer) flowFilePointer = filePointer + n
                buffer.positionSafe(pos)
                buffer.limitSafe(n)
            }

            return n
        } else {
            return null
        }
    }

    //    override suspend fun read(): Int? = withContext(Dispatchers.IO) { channel.read().takeIf(::readResultIsValid) }
    override suspend fun read(): Int? {
        bufferMutex.withLock {
            if (!buffer.hasRemaining()) {
                fill() ?: return null

                if (!buffer.hasRemaining()) return null
            }

            return buffer.get().toInt() and 0xFF
        }
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

            avail = fill() ?: return null

            if (!buffer.hasRemaining()) return null
        }


        val cnt = if (avail < len) avail else len
        buffer.get(b, off, cnt) //LEAVE THIS IN BRELLA
        return cnt
    }

    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if ((off or len or (off + len) or (b.size - (off + len))) < 0) {
            throw IndexOutOfBoundsException()
        } else if (len == 0) {
            return 0
        }

        var n = 0

        bufferMutex.withLock {
            while (true) {
                val nread = read1(b, off + n, len - n) ?: return if (n > 0) n else null
                if (nread <= 0)
                    return if (n == 0) nread else n
                n += nread
                if (n >= len)
                    return n
//            if (backing.available() ?: 0u <= 0u)
//                return n
            }
        }

        return n
    }

    suspend fun readAt(b: ByteArray, off: Int, len: Int, filePointer: Long): Int? {
        val bytebuf = ByteBuffer.wrap(b, off, len)

        return channel.readAwaitOrNull(bytebuf, filePointer)
    }

    override suspend fun peek(forward: Int): Int? {
        bufferMutex.withLock {
            if (!buffer.hasRemaining()) {
                fill()

                if (!buffer.hasRemaining()) return null
            }

            if (buffer.position() + forward >= buffer.limit()) {
                if ((buffer.position() + forward + 1) - count < buffer.capacity()) { /* Shuffle down */
                    val tmp = ByteArray(buffer.capacity() - buffer.position())
                    buffer.get(tmp)
                    buffer.clearSafe()
                    buffer.put(tmp)
                    fillPartial()
                    buffer.positionSafe(0)

                    if (buffer.remaining() < forward) return null //Probably hit the end of the file
                } else {
                    val absPosition = flowFilePointer + buffer.position() + forward
                    if (peekFilePointer != -1L && absPosition in peekFilePointer until peekFilePointer + peekBuffer.limit()) {
                        return peekBuffer.get((absPosition - peekFilePointer).toInt()).toInt() and 0xFF
                    } else {
                        peekFilePointer = absPosition
                        peekBuffer.clearSafe()
                        val read = fill(false, peekFilePointer)?.toLong()

                        if (read == null) {
                            peekFilePointer = -1
                        } else {
                            peekFilePointer += read
                        }

                        if (buffer.remaining() < forward) return null //Probably hit the end of the file
                        return peekBuffer.get(0).toInt() and 0xFF
                    }
                }
            }

            return buffer.get(buffer.position() + forward - 1).toInt() and 0xFF
        }
    }

    override suspend fun skip(n: ULong): ULong {
        bufferMutex.withLock {
            if (!buffer.hasRemaining()) {
                flowFilePointer += n.toLong()
                return n
            }

            if (buffer.remaining() < n.toInt()) {
                val avail = buffer.remaining()
                buffer.positionSafe(buffer.position() + avail)

                return avail.toULong() + (skip(n - avail.toULong()))
            } else {
                buffer.positionSafe(buffer.position() + n.toInt())

                return n
            }
        }
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = withContext(Dispatchers.IO) { channel.size().toULong() }
    override suspend fun position(): ULong =
        withContext(Dispatchers.IO) { bufferMutex.withLock { flowFilePointer - buffer.position() } }.toULong()

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        bufferMutex.withLock {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    if (pos in (flowFilePointer - buffer.limit()) until flowFilePointer) {
                        buffer.positionSafe((flowFilePointer - pos).toInt())
                    } else {
                        flowFilePointer = pos
                        buffer.clearSafe()
                    }
                }
                EnumSeekMode.FROM_POSITION -> {
                    if (buffer.position() + pos in 0..buffer.limit()) {
                        buffer.positionSafe((buffer.position() + pos).toInt())
                    } else {
                        flowFilePointer += buffer.position() + pos - buffer.limit()
                    }
                }
                EnumSeekMode.FROM_END -> {
                    val pos = size().toLong() - pos
                    if (pos in (flowFilePointer - buffer.limit()) until flowFilePointer) {
                        buffer.positionSafe((flowFilePointer - pos).toInt())
                    } else {
                        flowFilePointer = pos
                        buffer.clearSafe()
                    }
                }
            }

            return position()
        }
    }

    override suspend fun close() {
        super<PeekableInputFlow>.close()

        if (!closed) {
            if (localChannel) {
                withContext(Dispatchers.IO) { channel.close() }
            }
            closed = true
        }
    }
}