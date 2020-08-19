package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.coroutine.flow.ConflatingBufferedInputFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@ExperimentalUnsignedTypes
public actual class StdinInputFlow(location: String? = "stdin") : InputFlow, ConflatingBufferedInputFlow(location) {
    public companion object {
        //NOTE: The reason we use a collector rather than just reading stdin is because stdin blocks and cannot be interrupted
        //TODO: Check whether that's true of other inputstreams?

        private val _stdinFlow = MutableStateFlow(byteArrayOf())
        public val stdinFlow: StateFlow<ByteArray>
            get() = _stdinFlow

        public val stdinCollector: Job = GlobalScope.launch(Dispatchers.Main) {
            val buffer = ByteArray(8192)
            var read = 0

            while (isActive) {
                read = readFromStdin(buffer)
                _stdinFlow.value = buffer.copyOf(read)
            }
        }
    }

    private val collector: Job = GlobalScope.launch {
        stdinFlow.collect(channel::send)
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        collector.cancelAndJoin()
    }
}

public expect fun readFromStdin(buffer: ByteArray): Int