package dev.brella.kornea.io.common.flow

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.ReadWriteSemaphore
import dev.brella.kornea.toolkit.common.SharedStateRW
import dev.brella.kornea.toolkit.common.withWritePermit
import kotlinx.coroutines.sync.Semaphore

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_1_2_0_INDEV)
public open class MultiViewOutputFlow<O : OutputFlow> protected constructor(
    protected val backing: O,
    protected val semaphore: ReadWriteSemaphore,
    protected val instanceCount: SharedStateRW<Int>
) : BaseDataCloseable(), OutputFlow, PrintOutputFlow {
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

    protected suspend inline fun <T> access(crossinline block: suspend () -> T): T =
        semaphore.withWritePermit { block() }

    override suspend fun write(byte: Int): Unit = access { backing.write(byte) }
    override suspend fun write(b: ByteArray): Unit = access { backing.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit = access { backing.write(b, off, len) }

    override suspend fun flush(): Unit = access { backing.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        access {
            if (instanceCount.mutateState { it - 1 }.accessState { it == 0 })
                backing.close()
        }
    }
}