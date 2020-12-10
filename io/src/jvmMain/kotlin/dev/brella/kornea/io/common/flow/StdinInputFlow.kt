package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Url
import dev.brella.kornea.io.coroutine.flow.ConflatingBufferedInputFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.CopyOnWriteArrayList

@ExperimentalUnsignedTypes
public actual class StdinInputFlow(location: String? = "stdin") : InputFlow, ConflatingBufferedInputFlow(location) {
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

    override fun locationAsUrl(): KorneaResult<Url> = KorneaResult.empty()

    init {
        stdinChannels.add(channel)
    }
}