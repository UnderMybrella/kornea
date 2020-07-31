package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.toolkit.common.DataCloseable
import dev.brella.kornea.io.common.KorneaIO
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * A print flow that calls each [PrintFlow] function on [fan] in parallel
 * Note: If any fan flow fails, any fan flows that haven't yet completed will be cancelled
 */
@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_1_3_0_ALPHA)
public class FannedPrintFlow(private val fan: List<PrintFlow>) : BaseDataCloseable(), PrintFlow {
    override suspend fun print(value: Char): FannedPrintFlow {
        supervisorScope {
            fan.forEach { flow ->
                launch { flow.print(value) }
            }
        }

        return this
    }

    override suspend fun print(value: CharSequence?): FannedPrintFlow {
        supervisorScope {
            fan.forEach { flow ->
                launch { flow.print(value) }
            }
        }

        return this
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        supervisorScope {
            fan.forEach { flow ->
                if (flow is DataCloseable) launch { flow.close() }
            }
        }
    }
}