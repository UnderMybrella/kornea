package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.BinaryDataPool
import dev.brella.kornea.io.common.DataCloseableEventHandler
import dev.brella.kornea.io.common.DataPool
import dev.brella.kornea.io.jvm.JVMInputFlow
import dev.brella.kornea.io.jvm.clearSafe
import dev.brella.kornea.io.jvm.flipSafe
import dev.brella.kornea.toolkit.common.oneTimeMutableInline
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.CopyOnWriteArrayList

@ExperimentalUnsignedTypes
public actual class StandardInputFlow(location: String? = "stdin") : InputFlow, ConflatingBufferedInputFlow(location) {
    public companion object {
        //NOTE: The reason we use a collector rather than just reading stdin is because stdin blocks and cannot be interrupted
        //TODO: Check whether that's true of other inputstreams?

        public val stdinChannels: MutableList<Channel<ByteArray>> = CopyOnWriteArrayList()
        public val stdinCollector: Job = GlobalScope.launch(Dispatchers.IO) {
            val stdin = System.`in`
            val buffer = ByteArray(8192)
            var read = 0

            while (isActive) {
                read = stdin.read(buffer)

                stdinChannels.forEach { channel -> channel.offer(buffer.copyOf(read)) }
            }
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        stdinChannels.remove(channel)
    }

    init {
        stdinChannels.add(channel)
    }
}