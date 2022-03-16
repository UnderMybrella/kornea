package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.coroutine.flow.ConflatingBufferedInputFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

public actual class StdinInputFlow(location: String? = "stdin") : InputFlow, ConflatingBufferedInputFlow(location) {
    public companion object : CoroutineScope {
        //NOTE: The reason we use a collector rather than just reading stdin is because stdin blocks and cannot be interrupted
        //TODO: Check whether that's true of other inputstreams?

        override val coroutineContext: CoroutineContext = SupervisorJob()

        public val stdinChannels: MutableList<Channel<ByteArray>> = CopyOnWriteArrayList()
        public val stdinCollector: Job = launch(Dispatchers.IO) {
            val stdin = System.`in`
            val buffer = ByteArray(8192)
            var read: Int

            while (isActive) {
                read = stdin.read(buffer)

                stdinChannels.forEach { channel -> channel.trySend(buffer.copyOf(read)) }
            }
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        stdinChannels.remove(channel)
    }

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    init {
        stdinChannels.add(channel)
    }
}