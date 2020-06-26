package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

/**
 * An output flow that calls each [OutputFlow] function on [fan] in parallel
 * Note: If any fan flow fails, any fan flows that haven't yet completed will be cancelled
 */
@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_1_1_0_ALPHA)
public class FannedOutputFlow(private val fan: List<OutputFlow>) : BaseDataCloseable(), OutputFlow {
    override suspend fun write(byte: Int) {
        coroutineScope {
            fan.forEach { flow ->
                launch { flow.write(byte) }
            }
        }
    }

    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        coroutineScope {
            fan.forEach { flow ->
                launch { flow.write(b, off, len) }
            }
        }
    }

    override suspend fun flush() {
        coroutineScope {
            fan.forEach { flow ->
                launch { flow.flush() }
            }
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        coroutineScope {
            fan.forEach { flow ->
                launch { flow.close() }
            }
        }
    }
}