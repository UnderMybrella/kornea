package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Url
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.toolkit.common.accessState
import dev.brella.kornea.toolkit.common.mutateState
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.SharedStateRW

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
@ChangedSince(KorneaIO.VERSION_3_2_0_ALPHA)
public open class MultiViewOutputFlow<O : OutputFlow> protected constructor(
    backing: O,
    semaphore: ReadWriteSemaphore,
    protected val instanceCount: SharedStateRW<Int>
) : SynchronisedOutputFlow<O>(backing, semaphore, false) {
    public companion object {
        public suspend operator fun <O : OutputFlow> invoke(
            backing: O,
            semaphore: ReadWriteSemaphore = ReadWriteSemaphore(1),
            instanceCount: SharedStateRW<Int>
        ): MultiViewOutputFlow<O> {
            val flow = MultiViewOutputFlow(backing, semaphore, instanceCount)
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

    override fun locationAsUrl(): KorneaResult<Url> = KorneaResult.empty()
}