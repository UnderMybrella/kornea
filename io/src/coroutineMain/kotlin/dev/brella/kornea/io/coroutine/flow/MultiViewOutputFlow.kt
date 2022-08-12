package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.toolkit.common.accessState
import dev.brella.kornea.toolkit.common.mutateState
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.SharedStateRW

@ExperimentalKorneaToolkit
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
@ChangedSince(KorneaIO.VERSION_3_2_0_ALPHA)
public open class MultiViewOutputFlow<O : OutputFlow> protected constructor(
    backing: O,
    semaphore: ReadWriteSemaphore,
    protected val instanceCount: SharedStateRW<Int>,
    location: String? = "MultiViewOutputFlow(${backing.location})"
) : SynchronisedOutputFlow<O>(backing, semaphore, false, location) {
    public companion object {
        public suspend operator fun <O : OutputFlow> invoke(
            backing: O,
            semaphore: ReadWriteSemaphore = ReadWriteSemaphore(1),
            instanceCount: SharedStateRW<Int>,
            location: String? = "MultiViewOutputFlow(${backing.location})"
        ): MultiViewOutputFlow<O> {
            val flow = MultiViewOutputFlow(backing, semaphore, instanceCount, location)
            instanceCount.mutateState { it + 1 }
            return flow
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        access {
            if (instanceCount.mutateState { it - 1 }.accessState { it == 0 })
                output.close()
        }
    }

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()
}