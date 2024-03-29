package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.BufferedInputFlow
import dev.brella.kornea.io.common.flow.InputFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

/**
 * An output flow that calls each [OutputFlow] function on [fan] in parallel
 * Note: If any fan flow fails, any fan flows that haven't yet completed will be cancelled
 */
@AvailableSince(KorneaIO.VERSION_1_1_0_ALPHA)
public class FannedInputFlow(
    private val fan: List<InputFlow>,
    location: String? = fan.joinToString(prefix = "Fanned(", postfix = ")")
) : BufferedInputFlow(location) {
    public constructor(
        vararg fan: InputFlow,
        location: String? = fan.joinToString(prefix = "Fanned(", postfix = ")")
    ) : this(fan.toList(), location)

    private var fanPos: ULong = 0uL

    override suspend fun readImpl(b: ByteArray, off: Int, len: Int): Int? =
        coroutineScope {
            val channel = Channel<ByteArray>(Channel.RENDEZVOUS)

            val job = launch {
                supervisorScope {
                    fan.forEach { flow ->
                        launch {
                            val buffer = ByteArray(buffer.size - pos)
                            val limit = flow.read(buffer, 0, buffer.size)
                            if (limit != null) channel.send(buffer.copyOf(limit))
                        }
                    }
                }

                channel.close()
            }

            val readResult = channel.receiveCatching()
            if (readResult.isFailure) return@coroutineScope null
            val read = readResult.getOrThrow()

            job.cancel()
            read.copyInto(b, off)
            read.size
        }

    override suspend fun whenClosed() {
        super.whenClosed()

        supervisorScope {
            fan.forEach { flow ->
                launch { flow.close() }
            }
        }
    }

    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()
}