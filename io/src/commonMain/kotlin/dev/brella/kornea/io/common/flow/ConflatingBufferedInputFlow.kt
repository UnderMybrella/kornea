package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Add a conflating buffered input flow that provides a channel for sending data
 */
@AvailableSince(KorneaIO.VERSION_1_2_0_ALPHA)
public abstract class ConflatingBufferedInputFlow(location: String? = null): BufferedInputFlow(location) {
    protected val channel: Channel<ByteArray> = Channel(Channel.CONFLATED)
    protected val mutex: Mutex = Mutex()

    override suspend fun readImpl(b: ByteArray, off: Int, len: Int): Int? {
        val read = mutex.withLock { channel.receive() }
        if (read.size <= len) {
            read.copyInto(b, off)
            return read.size
        } else {
            mutex.withLock {
                val next = channel.receiveOrNull()
                read.copyInto(b, off, 0, len)

                if (next == null) {
                    channel.send(read.sliceArray(len until read.size))
                } else {
                    val buffer = ByteArray(read.size - len + next.size)
                    read.copyInto(buffer, 0, len)
                    next.copyInto(buffer, len)
                    channel.send(buffer)
                }

                return len
            }
        }
    }

    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null
}