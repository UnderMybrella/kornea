package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.DataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.ObservableDataCloseable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * An output flow that calls each [OutputFlow] function on [sequence] one after another
 */
@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_1_3_0_ALPHA)
public class SequentialPrintFlow(private val sequence: List<PrintFlow>) : BaseDataCloseable(), PrintFlow {
    override suspend fun print(value: Char): SequentialPrintFlow {
        sequence.forEach { flow -> flow.print(value) }

        return this
    }
    override suspend fun print(value: CharSequence?): PrintFlow {
        sequence.forEach { flow -> flow.print(value) }

        return this
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        sequence.forEach { flow -> if (flow is DataCloseable) flow.close() }
    }
}