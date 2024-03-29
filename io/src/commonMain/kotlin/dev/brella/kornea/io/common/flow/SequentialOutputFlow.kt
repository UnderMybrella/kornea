package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.composite.common.Composite
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri

/**
 * An output flow that calls each [OutputFlow] function on [sequence] one after another
 */
@AvailableSince(KorneaIO.VERSION_1_1_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class SequentialOutputFlow(
    private val sequence: List<OutputFlow>,
    override val location: String? = sequence.joinToString(
        prefix = "SequentialOutputFlow(",
        postfix = ")"
    ) { it.location.toString() }
) : BaseDataCloseable(), OutputFlow, OutputFlowState, IntFlowState by IntFlowState.base(), Composite.Empty {
    private var _counter = 0uL
    override suspend fun position(): ULong = _counter

    override suspend fun write(byte: Int) {
        _counter++
        sequence.forEach { flow -> flow.write(byte) }
    }

    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        _counter += len.toUInt()
        sequence.forEach { flow -> flow.write(b, off, len) }
    }

    override suspend fun flush() {
        sequence.forEach { flow -> flow.flush() }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        sequence.forEach { flow -> flow.close() }
    }

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()
}