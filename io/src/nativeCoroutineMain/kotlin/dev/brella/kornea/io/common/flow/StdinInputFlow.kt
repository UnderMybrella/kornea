package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.coroutine.flow.ConflatingBufferedInputFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

public actual class StdinInputFlow(location: String? = "stdin") : InputFlow, ConflatingBufferedInputFlow(location) {
    public companion object : CoroutineScope {
        //NOTE: The reason we use a collector rather than just reading stdin is because stdin blocks and cannot be interrupted
        //TODO: Check whether that's true of other inputstreams?

        override val coroutineContext: CoroutineContext = SupervisorJob()

        private val _stdinFlow = MutableStateFlow(byteArrayOf())
        public val stdinFlow: StateFlow<ByteArray>
            get() = _stdinFlow

        public val stdinCollector: Job = launch(Dispatchers.Main) {
            val buffer = ByteArray(8192)
            var read: Int

            while (isActive) {
                read = readFromStdin(buffer)
                _stdinFlow.value = buffer.copyOf(read)
            }
        }
    }

    private val collector: Job = launch {
        stdinFlow.collect(channel::send)
    }

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    override suspend fun whenClosed() {
        super.whenClosed()

        collector.cancelAndJoin()
    }
}

public expect fun readFromStdin(buffer: ByteArray): Int
public actual fun readFromStdin(buffer: ByteArray): Int {
    TODO("Not yet implemented")
}