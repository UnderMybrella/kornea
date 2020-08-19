package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.flow.*
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.SynchronisedBinaryView

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public inline fun BinaryPipeFlow(location: String? = null): PipeFlow<BinaryInputFlow, OutputFlowByDelegate<BinaryOutputFlow>> {
    val semaphore = ReadWriteSemaphore(1)
    val pipe = BinaryOutputFlow()

    return PipeFlow(
        BinaryInputFlow(SynchronisedBinaryView(pipe, semaphore), location = location),
        SynchronisedOutputFlow(BinaryOutputFlow(), semaphore)
    )
}